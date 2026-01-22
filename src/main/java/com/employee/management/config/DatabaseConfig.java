package com.employee.management.config;

import com.employee.management.model.Admin;
import com.employee.management.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Autowired
    private AdminRepository adminRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Create default admin if not exists
            if (!adminRepository.existsByUsername("admin")) {
                Admin defaultAdmin = new Admin("admin", "admin123");
                adminRepository.save(defaultAdmin);
                //System.out.println("Default admin created: username=admin, password=admin123");
            }
        };
    }
}
