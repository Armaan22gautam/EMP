package com.employee.management.controller;

import com.employee.management.dto.EmployeeDTO;
import com.employee.management.exception.DuplicateResourceException;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDTO;
    private MockHttpSession adminSession;
    private MockHttpSession employeeSession;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
        testEmployee.setId(1L);
        testEmployee.setCreatedAt(LocalDateTime.now());
        testEmployee.setUpdatedAt(LocalDateTime.now());

        testEmployeeDTO = new EmployeeDTO("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");

        // Set up admin session
        adminSession = new MockHttpSession();
        Admin admin = new Admin("admin", "admin123");
        admin.setId(1L);
        adminSession.setAttribute("user", admin);
        adminSession.setAttribute("userType", "admin");

        // Set up employee session
        employeeSession = new MockHttpSession();
        employeeSession.setAttribute("user", testEmployee);
        employeeSession.setAttribute("userType", "employee");
    }

    // ============ GET ALL EMPLOYEES ============

    @Test
    @DisplayName("GET /api/employees - admin gets paginated list")
    void getAllEmployees_asAdmin_returnsPaginatedList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(Arrays.asList(testEmployee), pageable, 1);
        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .session(adminSession)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/employees - employee gets 403")
    void getAllEmployees_asEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .session(employeeSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied. Admin privileges required."));
    }

    // ============ GET EMPLOYEE BY ID ============

    @Test
    @DisplayName("GET /api/employees/{id} - employee can view own profile")
    void getEmployeeById_asOwner_returnsEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(testEmployee));

        mockMvc.perform(get("/api/employees/1")
                        .session(employeeSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@company.com"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - employee cannot view other's profile")
    void getEmployeeById_asOtherEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/employees/2")
                        .session(employeeSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied. You can only view your own profile."));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - admin can view any profile")
    void getEmployeeById_asAdmin_returnsEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(testEmployee));

        mockMvc.perform(get("/api/employees/1")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - unauthenticated returns 401")
    void getEmployeeById_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    // ============ REGISTER EMPLOYEE ============

    @Test
    @DisplayName("POST /api/employees/register - valid data creates employee")
    void registerEmployee_validData_success() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(testEmployee);

        mockMvc.perform(post("/api/employees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee registered successfully"));
    }

    @Test
    @DisplayName("POST /api/employees/register - duplicate email returns error")
    void registerEmployee_duplicateEmail_returnsError() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDTO.class)))
                .thenThrow(new DuplicateResourceException("Email already exists"));

        mockMvc.perform(post("/api/employees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    // ============ UPDATE EMPLOYEE ============

    @Test
    @DisplayName("PUT /api/employees/{id} - admin can update employee")
    void updateEmployee_asAdmin_success() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeDTO.class))).thenReturn(testEmployee);

        mockMvc.perform(put("/api/employees/1")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"));
    }

    @Test
    @DisplayName("PUT /api/employees/{id} - employee cannot update")
    void updateEmployee_asEmployee_returns403() throws Exception {
        mockMvc.perform(put("/api/employees/1")
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied. Admin privileges required."));
    }

    // ============ DELETE EMPLOYEE ============

    @Test
    @DisplayName("DELETE /api/employees/{id} - admin can delete employee")
    void deleteEmployee_asAdmin_success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - employee cannot delete")
    void deleteEmployee_asEmployee_returns403() throws Exception {
        mockMvc.perform(delete("/api/employees/1")
                        .session(employeeSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - not found returns error")
    void deleteEmployee_notFound_returnsError() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/employees/99")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ============ PAYROLL DOWNLOAD ============

    @Test
    @DisplayName("GET /api/employees/{id}/payroll - employee downloads own payroll")
    void downloadPayroll_asOwner_success() throws Exception {
        when(employeeService.generatePayrollText(1L)).thenReturn("=== PAYROLL ===");

        mockMvc.perform(get("/api/employees/1/payroll")
                        .session(employeeSession))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().string("=== PAYROLL ==="));
    }

    @Test
    @DisplayName("GET /api/employees/{id}/payroll - employee cannot download other's payroll")
    void downloadPayroll_asOtherEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/employees/2/payroll")
                        .session(employeeSession))
                .andExpect(status().isForbidden());
    }
}
