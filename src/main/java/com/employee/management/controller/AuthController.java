package com.employee.management.controller;

import com.employee.management.dto.LoginRequest;
import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Object user = authService.authenticateUser(loginRequest);
            
            if (user != null) {
                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    session.setAttribute("user", admin);
                    session.setAttribute("userType", "admin");
                    response.put("success", true);
                    response.put("userType", "admin");
                    response.put("redirectUrl", "/admin-dashboard");
                    response.put("user", admin);
                } else if (user instanceof Employee) {
                    Employee employee = (Employee) user;
                    session.setAttribute("user", employee);
                    session.setAttribute("userType", "employee");
                    response.put("success", true);
                    response.put("userType", "employee");
                    response.put("redirectUrl", "/employee-dashboard");
                    response.put("user", employee);
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        session.invalidate();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");
        
        if (user != null) {
            response.put("authenticated", true);
            response.put("user", user);
            response.put("userType", userType);
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
