package com.employee.management.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    
    @GetMapping("/employee-dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");
        
        System.out.println("[DEBUG] Employee dashboard access - User: " + user + ", UserType: " + userType);
        
        if (user == null || !"employee".equals(userType)) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "employee-dashboard";
    }
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");
        
        System.out.println("[DEBUG] Admin dashboard access - User: " + user + ", UserType: " + userType);
        
        if (user == null || !"admin".equals(userType)) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "admin-dashboard";
    }
}
