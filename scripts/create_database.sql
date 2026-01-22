-- Create database if not exists
CREATE DATABASE IF NOT EXISTS employee_management_db;

-- Use the database
USE employee_management_db;

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    salary DOUBLE NOT NULL,
    address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create admins table
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ADMIN'
);

-- Insert default admin
INSERT IGNORE INTO admins (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN');

-- Insert sample employees for testing
INSERT IGNORE INTO employees (name, password, email, salary, address) VALUES
('John Doe', 'password123', 'john.doe@company.com', 50000.00, '123 Main St, City, State'),
('Jane Smith', 'password456', 'jane.smith@company.com', 55000.00, '456 Oak Ave, City, State'),
('Mike Johnson', 'password789', 'mike.johnson@company.com', 48000.00, '789 Pine Rd, City, State');
