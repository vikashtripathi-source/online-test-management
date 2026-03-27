package com.tech.test.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.DashboardDTO;
import com.tech.test.service.DashboardService;
import com.tech.test.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
@Import(com.tech.test.config.SecurityConfig.class)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DashboardService service;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private com.tech.test.security.CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetDashboard_Success() throws Exception {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setTotalOrders(5);
        dashboardDTO.setTotalTests(3);
        dashboardDTO.setAverageMarks(85.5);
        dashboardDTO.setBranch("CSE");

        when(service.getDashboard(anyLong())).thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(5))
                .andExpect(jsonPath("$.totalTests").value(3))
                .andExpect(jsonPath("$.averageMarks").value(85.5))
                .andExpect(jsonPath("$.branch").value("CSE"));
    }

    @Test
    @WithMockUser
    void testGetDashboard_NoData() throws Exception {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setTotalOrders(0);
        dashboardDTO.setTotalTests(0);
        dashboardDTO.setAverageMarks(0.0);
        dashboardDTO.setBranch("N/A");

        when(service.getDashboard(anyLong())).thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(0))
                .andExpect(jsonPath("$.totalTests").value(0))
                .andExpect(jsonPath("$.averageMarks").value(0.0))
                .andExpect(jsonPath("$.branch").value("N/A"));
    }
}
