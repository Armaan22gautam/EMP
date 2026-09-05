package com.employee.management.service;

import com.employee.management.dto.EmployeeDTO;
import com.employee.management.exception.DuplicateResourceException;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.model.Employee;
import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDTO;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
        testEmployee.setId(1L);
        testEmployee.setCreatedAt(LocalDateTime.now());
        testEmployee.setUpdatedAt(LocalDateTime.now());

        testEmployeeDTO = new EmployeeDTO("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
    }

    // ============ GET ALL EMPLOYEES ============

    @Test
    @DisplayName("getAllEmployees returns paginated employees")
    void getAllEmployees_returnsPaginatedList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(Arrays.asList(testEmployee));
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<Employee> result = employeeService.getAllEmployees(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(employeeRepository).findAll(pageable);
    }

    // ============ GET EMPLOYEE BY ID ============

    @Test
    @DisplayName("getEmployeeById returns employee when found")
    void getEmployeeById_found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    @DisplayName("getEmployeeById returns empty when not found")
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(99L);

        assertFalse(result.isPresent());
    }

    // ============ CREATE EMPLOYEE ============

    @Test
    @DisplayName("createEmployee successfully creates new employee")
    void createEmployee_success() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByName(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee result = employeeService.createEmployee(testEmployeeDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("createEmployee throws DuplicateResourceException when email exists")
    void createEmployee_duplicateEmail_throwsException() {
        when(employeeRepository.existsByEmail("john@company.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.createEmployee(testEmployeeDTO)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("createEmployee throws DuplicateResourceException when name exists")
    void createEmployee_duplicateName_throwsException() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByName("John Doe")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.createEmployee(testEmployeeDTO)
        );

        assertEquals("Employee name already exists", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    // ============ UPDATE EMPLOYEE ============

    @Test
    @DisplayName("updateEmployee successfully updates existing employee")
    void updateEmployee_success() {
        EmployeeDTO updateDTO = new EmployeeDTO("John Updated", "newpass", "john@company.com", 60000.0, "456 Oak Ave");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByName("John Updated")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee result = employeeService.updateEmployee(1L, updateDTO);

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("updateEmployee throws EmployeeNotFoundException when not found")
    void updateEmployee_notFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(99L, testEmployeeDTO)
        );
    }

    @Test
    @DisplayName("updateEmployee throws DuplicateResourceException when new email exists")
    void updateEmployee_duplicateEmail_throwsException() {
        EmployeeDTO updateDTO = new EmployeeDTO("John Doe", "pass", "existing@company.com", 60000.0, "addr");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByEmail("existing@company.com")).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.updateEmployee(1L, updateDTO)
        );
    }

    // ============ DELETE EMPLOYEE ============

    @Test
    @DisplayName("deleteEmployee successfully deletes existing employee")
    void deleteEmployee_success() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteEmployee throws EmployeeNotFoundException when not found")
    void deleteEmployee_notFound_throwsException() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(99L)
        );
    }

    // ============ GENERATE PAYROLL TEXT ============

    @Test
    @DisplayName("generatePayrollText returns formatted payroll text")
    void generatePayrollText_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        String payroll = employeeService.generatePayrollText(1L);

        assertNotNull(payroll);
        assertTrue(payroll.contains("John Doe"));
        assertTrue(payroll.contains("john@company.com"));
        assertTrue(payroll.contains("50000.00"));
        assertTrue(payroll.contains("EMPLOYEE PAYROLL INFORMATION"));
    }

    @Test
    @DisplayName("generatePayrollText throws EmployeeNotFoundException when not found")
    void generatePayrollText_notFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.generatePayrollText(99L)
        );
    }
}
