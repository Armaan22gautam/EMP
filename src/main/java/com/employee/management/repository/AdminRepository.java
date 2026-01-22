package com.employee.management.repository;

import com.employee.management.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByUsername(String username);
    
    @Query("SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password")
    Optional<Admin> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    boolean existsByUsername(String username);
}
