package com.employee.management.service;

import com.employee.management.dto.EmployeeDTO;
import com.employee.management.exception.DuplicateResourceException;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.model.Employee;
import com.employee.management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public Employee createEmployee(EmployeeDTO employeeDTO) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Check if name already exists
        if (employeeRepository.existsByName(employeeDTO.getName())) {
            throw new DuplicateResourceException("Employee name already exists");
        }

        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setPassword(employeeDTO.getPassword());
        employee.setEmail(employeeDTO.getEmail());
        employee.setSalary(employeeDTO.getSalary());
        employee.setAddress(employeeDTO.getAddress());

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Check if email is being changed and if new email already exists
        if (!employee.getEmail().equals(employeeDTO.getEmail()) &&
                employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Check if name is being changed and if new name already exists
        if (!employee.getName().equals(employeeDTO.getName()) &&
                employeeRepository.existsByName(employeeDTO.getName())) {
            throw new DuplicateResourceException("Employee name already exists");
        }

        employee.setName(employeeDTO.getName());
        employee.setPassword(employeeDTO.getPassword());
        employee.setEmail(employeeDTO.getEmail());
        employee.setSalary(employeeDTO.getSalary());
        employee.setAddress(employeeDTO.getAddress());

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
        } else {
            throw new EmployeeNotFoundException(id);
        }
    }

    public String generatePayrollText(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        StringBuilder payroll = new StringBuilder();
        payroll.append("=== EMPLOYEE PAYROLL INFORMATION ===\n\n");
        payroll.append("Employee ID: ").append(employee.getId()).append("\n");
        payroll.append("Name: ").append(employee.getName()).append("\n");
        payroll.append("Email: ").append(employee.getEmail()).append("\n");
        payroll.append("Salary: $").append(String.format("%.2f", employee.getSalary())).append("\n");
        payroll.append("Address: ").append(employee.getAddress()).append("\n");
        payroll.append("Created Date: ").append(employee.getCreatedAt()).append("\n");
        payroll.append("Last Updated: ").append(employee.getUpdatedAt()).append("\n");
        payroll.append("\n=== END OF PAYROLL ===");

        return payroll.toString();
    }
}
