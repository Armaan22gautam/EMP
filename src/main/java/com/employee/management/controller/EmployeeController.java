package com.employee.management.controller;

import com.employee.management.dto.EmployeeDTO;
import com.employee.management.model.Employee;
import com.employee.management.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<?> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        String userType = (String) session.getAttribute("userType");

        if (!"admin".equals(userType)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Admin privileges required.");
            return ResponseEntity.status(403).body(error);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employeePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }

        if ("employee".equals(userType)) {
            Employee sessionEmployee = (Employee) user;
            if (!sessionEmployee.getId().equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. You can only view your own profile.");
                return ResponseEntity.status(403).body(error);
            }
        }

        return employeeService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok().body(employee))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            Employee employee = employeeService.createEmployee(employeeDTO);
            response.put("success", true);
            response.put("message", "Employee registered successfully");
            response.put("employee", employee);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is admin
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.put("success", false);
            response.put("message", "Access denied. Admin privileges required.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            Employee employee = employeeService.updateEmployee(id, employeeDTO);
            response.put("success", true);
            response.put("message", "Employee updated successfully");
            response.put("employee", employee);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is admin
        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.put("success", false);
            response.put("message", "Access denied. Admin privileges required.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            employeeService.deleteEmployee(id);
            response.put("success", true);
            response.put("message", "Employee deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/payroll")
    public void downloadPayroll(@PathVariable Long id, HttpServletResponse response, HttpSession session)
            throws IOException {
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
            if (!sessionEmployee.getId().equals(id)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }

        try {
            String payrollText = employeeService.generatePayrollText(id);

            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=payroll_employee_" + id + ".txt");
            response.getWriter().write(payrollText);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found");
        }
    }
}
