package com.employee.management.controller;

import com.employee.management.model.Employee;
import com.employee.management.service.PayrollService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    
    @Autowired
    private PayrollService payrollService;
    
    @GetMapping("/download/{employeeId}")
    public void downloadPayroll(@PathVariable Long employeeId, HttpServletResponse response, HttpSession session) throws IOException {
        // Check if user is authenticated
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        // If employee, can only download their own payroll
        if ("employee".equals(userType)) {
            Employee sessionEmployee = (Employee) user;
            if (!sessionEmployee.getId().equals(employeeId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }
        
        try {
            String payrollContent = payrollService.generateDetailedPayroll(employeeId);
            String fileName = payrollService.getPayrollFileName(employeeId);
            
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getWriter().write(payrollContent);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found");
        }
    }
    
    @GetMapping("/preview/{employeeId}")
    public ResponseEntity<Map<String, Object>> previewPayroll(@PathVariable Long employeeId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");
        
        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication required");
            return ResponseEntity.status(401).body(response);
        }
        
        // If employee, can only preview their own payroll
        if ("employee".equals(userType)) {
            Employee sessionEmployee = (Employee) user;
            if (!sessionEmployee.getId().equals(employeeId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
        }
        
        try {
            String payrollContent = payrollService.generateDetailedPayroll(employeeId);
            response.put("success", true);
            response.put("content", payrollContent);
            response.put("fileName", payrollService.getPayrollFileName(employeeId));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Employee not found");
        }
        
        return ResponseEntity.ok(response);
    }
}
