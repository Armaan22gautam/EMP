package com.employee.management.service;

import com.employee.management.model.Employee;
import com.employee.management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PayrollService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public String generateDetailedPayroll(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            return generatePayrollDocument(employee);
        }
        throw new RuntimeException("Employee not found");
    }
    
    private String generatePayrollDocument(Employee employee) {
        StringBuilder payroll = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Header
        payroll.append("=====================================\n");
        payroll.append("       EMPLOYEE PAYROLL DOCUMENT     \n");
        payroll.append("=====================================\n\n");
        
        // Company Info
        payroll.append("Company: Employee Management System\n");
        payroll.append("Generated on: ").append(LocalDateTime.now().format(formatter)).append("\n\n");
        
        // Employee Information
        payroll.append("EMPLOYEE INFORMATION:\n");
        payroll.append("---------------------\n");
        payroll.append("Employee ID: ").append(employee.getId()).append("\n");
        payroll.append("Full Name: ").append(employee.getName()).append("\n");
        payroll.append("Email Address: ").append(employee.getEmail()).append("\n");
        payroll.append("Address: ").append(employee.getAddress()).append("\n");
        payroll.append("Employment Date: ").append(employee.getCreatedAt().format(formatter)).append("\n");
        payroll.append("Last Updated: ").append(employee.getUpdatedAt().format(formatter)).append("\n\n");
        
        // Salary Information
        payroll.append("SALARY INFORMATION:\n");
        payroll.append("-------------------\n");
        payroll.append("Annual Salary: $").append(String.format("%.2f", employee.getSalary())).append("\n");
        payroll.append("Monthly Salary: $").append(String.format("%.2f", employee.getSalary() / 12)).append("\n");
        payroll.append("Weekly Salary: $").append(String.format("%.2f", employee.getSalary() / 52)).append("\n");
        payroll.append("Daily Salary: $").append(String.format("%.2f", employee.getSalary() / 365)).append("\n\n");
        
        // Tax Information (Sample calculations)
        double annualSalary = employee.getSalary();
        double federalTax = annualSalary * 0.22; // 22% federal tax rate
        double stateTax = annualSalary * 0.05; // 5% state tax rate
        double socialSecurity = annualSalary * 0.062; // 6.2% social security
        double medicare = annualSalary * 0.0145; // 1.45% medicare
        double totalDeductions = federalTax + stateTax + socialSecurity + medicare;
        double netSalary = annualSalary - totalDeductions;
        
        payroll.append("TAX DEDUCTIONS (ESTIMATED):\n");
        payroll.append("----------------------------\n");
        payroll.append("Federal Tax (22%): $").append(String.format("%.2f", federalTax)).append("\n");
        payroll.append("State Tax (5%): $").append(String.format("%.2f", stateTax)).append("\n");
        payroll.append("Social Security (6.2%): $").append(String.format("%.2f", socialSecurity)).append("\n");
        payroll.append("Medicare (1.45%): $").append(String.format("%.2f", medicare)).append("\n");
        payroll.append("Total Deductions: $").append(String.format("%.2f", totalDeductions)).append("\n");
        payroll.append("Net Annual Salary: $").append(String.format("%.2f", netSalary)).append("\n\n");
        
        // Monthly breakdown
        payroll.append("MONTHLY BREAKDOWN:\n");
        payroll.append("------------------\n");
        payroll.append("Gross Monthly: $").append(String.format("%.2f", annualSalary / 12)).append("\n");
        payroll.append("Monthly Deductions: $").append(String.format("%.2f", totalDeductions / 12)).append("\n");
        payroll.append("Net Monthly: $").append(String.format("%.2f", netSalary / 12)).append("\n\n");
        
        // Footer
        payroll.append("=====================================\n");
        payroll.append("This document is generated automatically\n");
        payroll.append("and is for informational purposes only.\n");
        payroll.append("=====================================\n");
        
        return payroll.toString();
    }
    
    public String getPayrollFileName(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            String sanitizedName = employee.getName().replaceAll("[^a-zA-Z0-9]", "_");
            return "payroll_" + sanitizedName + "_" + employeeId + "_" + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        }
        return "payroll_" + employeeId + ".txt";
    }
}
