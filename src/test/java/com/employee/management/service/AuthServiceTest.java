package com.employee.management.service;

import com.employee.management.dto.LoginRequest;
import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.repository.AdminRepository;
import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AuthService authService;

    private Admin testAdmin;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin("admin", "admin123");
        testAdmin.setId(1L);

        testEmployee = new Employee("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
        testEmployee.setId(1L);
    }

    // ============ AUTHENTICATE USER ============

    @Test
    @DisplayName("authenticateUser returns Admin for valid admin credentials")
    void authenticateUser_adminSuccess() {
        LoginRequest request = new LoginRequest("admin", "admin123", "admin");
        when(adminRepository.findByUsernameAndPassword("admin", "admin123"))
                .thenReturn(Optional.of(testAdmin));

        Object result = authService.authenticateUser(request);

        assertNotNull(result);
        assertInstanceOf(Admin.class, result);
        assertEquals("admin", ((Admin) result).getUsername());
    }

    @Test
    @DisplayName("authenticateUser returns Employee for valid employee credentials")
    void authenticateUser_employeeSuccess() {
        LoginRequest request = new LoginRequest("John Doe", "password123", "employee");
        when(employeeRepository.findByNameAndPassword("John Doe", "password123"))
                .thenReturn(Optional.of(testEmployee));

        Object result = authService.authenticateUser(request);

        assertNotNull(result);
        assertInstanceOf(Employee.class, result);
        assertEquals("John Doe", ((Employee) result).getName());
    }

    @Test
    @DisplayName("authenticateUser returns null for invalid admin credentials")
    void authenticateUser_invalidAdmin_returnsNull() {
        LoginRequest request = new LoginRequest("admin", "wrongpass", "admin");
        when(adminRepository.findByUsernameAndPassword("admin", "wrongpass"))
                .thenReturn(Optional.empty());

        Object result = authService.authenticateUser(request);

        assertNull(result);
    }

    @Test
    @DisplayName("authenticateUser returns null for invalid employee credentials")
    void authenticateUser_invalidEmployee_returnsNull() {
        LoginRequest request = new LoginRequest("Nobody", "wrongpass", "employee");
        when(employeeRepository.findByNameAndPassword("Nobody", "wrongpass"))
                .thenReturn(Optional.empty());

        Object result = authService.authenticateUser(request);

        assertNull(result);
    }

    // ============ HELPER METHODS ============

    @Test
    @DisplayName("isAdmin returns true for valid admin credentials")
    void isAdmin_valid_returnsTrue() {
        when(adminRepository.findByUsernameAndPassword("admin", "admin123"))
                .thenReturn(Optional.of(testAdmin));

        assertTrue(authService.isAdmin("admin", "admin123"));
    }

    @Test
    @DisplayName("isAdmin returns false for invalid credentials")
    void isAdmin_invalid_returnsFalse() {
        when(adminRepository.findByUsernameAndPassword("admin", "wrong"))
                .thenReturn(Optional.empty());

        assertFalse(authService.isAdmin("admin", "wrong"));
    }

    @Test
    @DisplayName("isEmployee returns true for valid employee credentials")
    void isEmployee_valid_returnsTrue() {
        when(employeeRepository.findByNameAndPassword("John Doe", "password123"))
                .thenReturn(Optional.of(testEmployee));

        assertTrue(authService.isEmployee("John Doe", "password123"));
    }

    @Test
    @DisplayName("getEmployeeByName returns employee when found")
    void getEmployeeByName_found() {
        when(employeeRepository.findByName("John Doe"))
                .thenReturn(Optional.of(testEmployee));

        Employee result = authService.getEmployeeByName("John Doe");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    @DisplayName("getEmployeeByName returns null when not found")
    void getEmployeeByName_notFound() {
        when(employeeRepository.findByName("Nobody"))
                .thenReturn(Optional.empty());

        assertNull(authService.getEmployeeByName("Nobody"));
    }

    @Test
    @DisplayName("getAdminByUsername returns admin when found")
    void getAdminByUsername_found() {
        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(testAdmin));

        Admin result = authService.getAdminByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }
}
