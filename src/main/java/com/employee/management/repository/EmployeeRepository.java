package com.employee.management.repository;

import com.employee.management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByName(String name);
    
    @Query("SELECT e FROM Employee e WHERE e.name = :name AND e.password = :password")
    Optional<Employee> findByNameAndPassword(@Param("name") String name, @Param("password") String password);
    
    boolean existsByEmail(String email);
    
    boolean existsByName(String name);
}
