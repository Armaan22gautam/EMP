package com.employee.management.controller;

import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayrollController.class)
@AutoConfigureMockMvc(addFilters = false)
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    private MockHttpSession adminSession;
    private MockHttpSession employeeSession;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
        testEmployee.setId(1L);

        adminSession = new MockHttpSession();
        Admin admin = new Admin("admin", "admin123");
        admin.setId(1L);
        adminSession.setAttribute("user", admin);
        adminSession.setAttribute("userType", "admin");

        employeeSession = new MockHttpSession();
        employeeSession.setAttribute("user", testEmployee);
        employeeSession.setAttribute("userType", "employee");
    }

    // ============ DOWNLOAD PAYROLL ============

    @Test
    @DisplayName("GET /api/payroll/download/{id} - admin downloads payroll")
    void downloadPayroll_asAdmin_success() throws Exception {
        when(payrollService.generateDetailedPayroll(1L)).thenReturn("=== PAYROLL DOCUMENT ===");
        when(payrollService.getPayrollFileName(1L)).thenReturn("payroll_John_Doe_1_20240101.txt");

        mockMvc.perform(get("/api/payroll/download/1")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=payroll_John_Doe_1_20240101.txt"))
                .andExpect(content().string("=== PAYROLL DOCUMENT ==="));
    }

    @Test
    @DisplayName("GET /api/payroll/download/{id} - employee downloads own payroll")
    void downloadPayroll_asOwner_success() throws Exception {
        when(payrollService.generateDetailedPayroll(1L)).thenReturn("=== PAYROLL DOCUMENT ===");
        when(payrollService.getPayrollFileName(1L)).thenReturn("payroll_John_Doe_1_20240101.txt");

        mockMvc.perform(get("/api/payroll/download/1")
                        .session(employeeSession))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));
    }

    @Test
    @DisplayName("GET /api/payroll/download/{id} - employee cannot download other's payroll")
    void downloadPayroll_asOtherEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/payroll/download/2")
                        .session(employeeSession))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/payroll/download/{id} - unauthenticated returns 401")
    void downloadPayroll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/payroll/download/1"))
                .andExpect(status().isUnauthorized());
    }

    // ============ PREVIEW PAYROLL ============

    @Test
    @DisplayName("GET /api/payroll/preview/{id} - admin previews payroll")
    void previewPayroll_asAdmin_success() throws Exception {
        when(payrollService.generateDetailedPayroll(1L)).thenReturn("=== PAYROLL ===");
        when(payrollService.getPayrollFileName(1L)).thenReturn("payroll.txt");

        mockMvc.perform(get("/api/payroll/preview/1")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").value("=== PAYROLL ==="))
                .andExpect(jsonPath("$.fileName").value("payroll.txt"));
    }

    @Test
    @DisplayName("GET /api/payroll/preview/{id} - employee previews own payroll")
    void previewPayroll_asOwner_success() throws Exception {
        when(payrollService.generateDetailedPayroll(1L)).thenReturn("=== PAYROLL ===");
        when(payrollService.getPayrollFileName(1L)).thenReturn("payroll.txt");

        mockMvc.perform(get("/api/payroll/preview/1")
                        .session(employeeSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/payroll/preview/{id} - employee cannot preview other's payroll")
    void previewPayroll_asOtherEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/payroll/preview/2")
                        .session(employeeSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("GET /api/payroll/preview/{id} - unauthenticated returns 401")
    void previewPayroll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/payroll/preview/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }
}
