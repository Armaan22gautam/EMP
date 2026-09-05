package com.employee.management.controller;

import com.employee.management.dto.LoginRequest;
import com.employee.management.model.Admin;
import com.employee.management.model.Employee;
import com.employee.management.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // ============ LOGIN ============

    @Test
    @DisplayName("POST /api/auth/login - admin login success")
    void login_adminSuccess() throws Exception {
        Admin admin = new Admin("admin", "admin123");
        admin.setId(1L);
        LoginRequest request = new LoginRequest("admin", "admin123", "admin");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(admin);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userType").value("admin"))
                .andExpect(jsonPath("$.redirectUrl").value("/admin-dashboard"));
    }

    @Test
    @DisplayName("POST /api/auth/login - employee login success")
    void login_employeeSuccess() throws Exception {
        Employee employee = new Employee("John Doe", "password123", "john@company.com", 50000.0, "123 Main St");
        employee.setId(1L);
        LoginRequest request = new LoginRequest("John Doe", "password123", "employee");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(employee);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userType").value("employee"))
                .andExpect(jsonPath("$.redirectUrl").value("/employee-dashboard"));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid credentials")
    void login_invalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("nobody", "wrongpass", "employee");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    // ============ LOGOUT ============

    @Test
    @DisplayName("POST /api/auth/logout - success")
    void logout_success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new Admin("admin", "admin123"));
        session.setAttribute("userType", "admin");

        mockMvc.perform(post("/api/auth/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    // ============ SESSION ============

    @Test
    @DisplayName("GET /api/auth/session - authenticated")
    void getSession_authenticated() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Admin admin = new Admin("admin", "admin123");
        session.setAttribute("user", admin);
        session.setAttribute("userType", "admin");

        mockMvc.perform(get("/api/auth/session")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userType").value("admin"));
    }

    @Test
    @DisplayName("GET /api/auth/session - not authenticated")
    void getSession_notAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }
}
