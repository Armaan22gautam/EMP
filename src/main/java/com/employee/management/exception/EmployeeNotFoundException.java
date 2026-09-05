package com.employee.management.exception;

public class EmployeeNotFoundException extends RuntimeException {
    
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
    
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
