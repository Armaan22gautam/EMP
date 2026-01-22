package com.employee.management.service;

import com.employee.management.dto.LoginRequest;
import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.repository.AdminRepository;
import com.employee.management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    public Object authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String userType = loginRequest.getUserType();
        
        if ("admin".equals(userType)) {
            Optional<Admin> admin = adminRepository.findByUsernameAndPassword(username, password);
            return admin.orElse(null);
        } else {
            Optional<Employee> employee = employeeRepository.findByNameAndPassword(username, password);
            return employee.orElse(null);
        }
    }
    
    public boolean isAdmin(String username, String password) {
        return adminRepository.findByUsernameAndPassword(username, password).isPresent();
    }
    
    public boolean isEmployee(String name, String password) {
        return employeeRepository.findByNameAndPassword(name, password).isPresent();
    }
    
    public Employee getEmployeeByName(String name) {
        return employeeRepository.findByName(name).orElse(null);
    }
    
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }
}
