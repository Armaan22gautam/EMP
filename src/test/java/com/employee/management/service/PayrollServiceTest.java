package com.employee.management.service;

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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PayrollService payrollService;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("Jane Smith", "pass456", "jane@company.com", 60000.0, "456 Oak Ave");
        testEmployee.setId(2L);
        testEmployee.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));
        testEmployee.setUpdatedAt(LocalDateTime.of(2024, 6, 1, 14, 30));
    }

    // ============ GENERATE DETAILED PAYROLL ============

    @Test
    @DisplayName("generateDetailedPayroll returns comprehensive payroll document")
    void generateDetailedPayroll_success() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));

        String payroll = payrollService.generateDetailedPayroll(2L);

        assertNotNull(payroll);
        // Verify header
        assertTrue(payroll.contains("EMPLOYEE PAYROLL DOCUMENT"));
        // Verify employee info
        assertTrue(payroll.contains("Jane Smith"));
        assertTrue(payroll.contains("jane@company.com"));
        assertTrue(payroll.contains("456 Oak Ave"));
        // Verify salary breakdown
        assertTrue(payroll.contains("Annual Salary: $60000.00"));
        assertTrue(payroll.contains("Monthly Salary: $5000.00"));
        // Verify tax calculations
        assertTrue(payroll.contains("Federal Tax (22%)"));
        assertTrue(payroll.contains("State Tax (5%)"));
        assertTrue(payroll.contains("Social Security (6.2%)"));
        assertTrue(payroll.contains("Medicare (1.45%)"));
        // Verify net salary exists
        assertTrue(payroll.contains("Net Annual Salary"));
        assertTrue(payroll.contains("Net Monthly"));
        // Verify footer
        assertTrue(payroll.contains("informational purposes only"));
    }

    @Test
    @DisplayName("generateDetailedPayroll tax calculations are correct")
    void generateDetailedPayroll_taxCalculationsCorrect() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));

        String payroll = payrollService.generateDetailedPayroll(2L);

        // 60000 * 0.22 = 13200
        assertTrue(payroll.contains("$13200.00"));
        // 60000 * 0.05 = 3000
        assertTrue(payroll.contains("$3000.00"));
        // 60000 * 0.062 = 3720
        assertTrue(payroll.contains("$3720.00"));
        // 60000 * 0.0145 = 870
        assertTrue(payroll.contains("$870.00"));
    }

    @Test
    @DisplayName("generateDetailedPayroll throws EmployeeNotFoundException when not found")
    void generateDetailedPayroll_notFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> payrollService.generateDetailedPayroll(99L)
        );
    }

    // ============ GET PAYROLL FILE NAME ============

    @Test
    @DisplayName("getPayrollFileName generates correct filename")
    void getPayrollFileName_generatesCorrectName() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));

        String fileName = payrollService.getPayrollFileName(2L);

        assertNotNull(fileName);
        assertTrue(fileName.startsWith("payroll_Jane_Smith_2_"));
        assertTrue(fileName.endsWith(".txt"));
    }

    @Test
    @DisplayName("getPayrollFileName returns fallback when employee not found")
    void getPayrollFileName_notFound_returnsFallback() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        String fileName = payrollService.getPayrollFileName(99L);

        assertEquals("payroll_99.txt", fileName);
    }

    @Test
    @DisplayName("getPayrollFileName sanitizes special characters in name")
    void getPayrollFileName_sanitizesSpecialChars() {
        Employee specialEmployee = new Employee("O'Brien Jr.", "pass", "obrien@co.com", 45000.0, "addr");
        specialEmployee.setId(5L);
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(specialEmployee));

        String fileName = payrollService.getPayrollFileName(5L);

        // Special characters in name should be replaced with underscores
        String namePart = fileName.substring(0, fileName.lastIndexOf('.'));
        assertFalse(namePart.contains("'"));
        assertFalse(namePart.contains("."));
        assertTrue(fileName.contains("O_Brien_Jr_"));
        assertTrue(fileName.endsWith(".txt"));
    }
}
