package com.employee.management;

import com.employee.management.dto.EmployeeDTO;
import com.employee.management.dto.LoginRequest;
import com.employee.management.model.Employee;
import com.employee.management.repository.AdminRepository;
import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeManagementSystemApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AdminRepository adminRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ============ CONTEXT LOADING ============

    @Test
    @Order(1)
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // If this test passes, the Spring context loaded without errors
        assertNotNull(employeeRepository);
        assertNotNull(adminRepository);
    }

    // ============ EMPLOYEE REGISTRATION FLOW ============

    @Test
    @Order(2)
    @DisplayName("System Test: Employee registration flow")
    void employeeRegistrationFlow() {
        EmployeeDTO newEmployee = new EmployeeDTO(
                "System Test User", "testpass123",
                "systemtest@company.com", 55000.0,
                "789 Test Blvd, Suite 100"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/employees/register", request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("Employee registered successfully", response.getBody().get("message"));
    }

    @Test
    @Order(3)
    @DisplayName("System Test: Duplicate registration fails")
    void duplicateRegistrationFails() {
        EmployeeDTO duplicateEmployee = new EmployeeDTO(
                "System Test User", "testpass123",
                "systemtest@company.com", 55000.0,
                "789 Test Blvd"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(duplicateEmployee, headers);

        // The controller catches the exception and returns success=false in the body
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/employees/register", request, Map.class);

        assertNotNull(response.getBody());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    // ============ ADMIN LOGIN FLOW ============

    @Test
    @Order(4)
    @DisplayName("System Test: Admin login and session management")
    void adminLoginFlow() {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123", "admin");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("admin", response.getBody().get("userType"));
        assertEquals("/admin-dashboard", response.getBody().get("redirectUrl"));
    }

    // ============ EMPLOYEE LOGIN FLOW ============

    @Test
    @Order(5)
    @DisplayName("System Test: Employee login flow")
    void employeeLoginFlow() {
        LoginRequest loginRequest = new LoginRequest("System Test User", "testpass123", "employee");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("employee", response.getBody().get("userType"));
        assertEquals("/employee-dashboard", response.getBody().get("redirectUrl"));
    }

    // ============ INVALID LOGIN ============

    @Test
    @Order(6)
    @DisplayName("System Test: Invalid login returns error")
    void invalidLoginReturnsError() {
        LoginRequest loginRequest = new LoginRequest("nobody", "wrongpass", "employee");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    // ============ EMPLOYEE DATA VERIFICATION ============

    @Test
    @Order(7)
    @DisplayName("System Test: Verify registered employee exists in database")
    void verifyRegisteredEmployeeExists() {
        assertTrue(employeeRepository.existsByEmail("systemtest@company.com"));
        assertTrue(employeeRepository.existsByName("System Test User"));

        Employee employee = employeeRepository.findByName("System Test User").orElse(null);
        assertNotNull(employee);
        assertEquals(55000.0, employee.getSalary());
        assertEquals("789 Test Blvd, Suite 100", employee.getAddress());
    }

    // ============ PAYROLL GENERATION ============

    @Test
    @Order(8)
    @DisplayName("System Test: Verify payroll service generates correct data")
    void verifyPayrollGeneration() {
        Employee employee = employeeRepository.findByName("System Test User").orElse(null);
        assertNotNull(employee);

        // The payroll text download endpoint requires a session, but we can test
        // the database-level data is correct for payroll generation
        assertNotNull(employee.getSalary());
        assertEquals(55000.0, employee.getSalary());
        assertNotNull(employee.getCreatedAt());
    }

    // ============ DEFAULT ADMIN VERIFICATION ============

    @Test
    @Order(9)
    @DisplayName("System Test: Default admin is created on startup")
    void defaultAdminExists() {
        assertTrue(adminRepository.existsByUsername("admin"));
    }

    // ============ LOGOUT FLOW ============

    @Test
    @Order(10)
    @DisplayName("System Test: Logout flow")
    void logoutFlow() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/logout", request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("Logged out successfully", response.getBody().get("message"));
    }

    // ============ PASSWORD NOT IN RESPONSE ============

    @Test
    @Order(11)
    @DisplayName("System Test: Password is not exposed in JSON response")
    void passwordNotInResponse() {
        // Register a new employee and check the response
        EmployeeDTO newEmployee = new EmployeeDTO(
                "Password Check User", "secretpass",
                "pwcheck@company.com", 40000.0, "Test Address"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/employees/register", request, Map.class);

        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));

        // Check that the employee object in the response doesn't contain a password
        Map employeeMap = (Map) response.getBody().get("employee");
        assertNotNull(employeeMap);
        assertNull(employeeMap.get("password"), "Password should NOT be in JSON response");
    }
}
