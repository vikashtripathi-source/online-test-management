package com.tech.test.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.enums.Branch;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import com.tech.test.util.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExamController.class)
@Import(com.tech.test.config.SecurityConfig.class)
class ExamControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ExamService service;
    @MockBean private KafkaProducerService kafkaProducerService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private com.tech.test.security.CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

    private StudentTestRecordDTO sample() {
        StudentTestRecordDTO dto = new StudentTestRecordDTO();
        dto.setId(1L);
        dto.setRollNumber("12345");
        dto.setBranch(Branch.CSE);
        dto.setMarks(85);
        dto.setStudentId(1L);
        return dto;
    }

    @Test
    @WithMockUser
    void testSaveStudentTestRecord() throws Exception {

        StudentTestRecordDTO saved = sample();
        saved.setId(1L);

        when(service.saveStudentTestRecord(any(StudentTestRecordDTO.class))).thenReturn(saved);

        mockMvc.perform(
                        post("/api/exams/student-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sample())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void testGetRecordsByBranch() throws Exception {

        List<StudentTestRecordDTO> records = List.of(sample());

        when(service.getRecordsByBranch(Branch.CSE)).thenReturn(records);

        mockMvc.perform(get("/api/exams/student-records/branch/CSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void testUpdateStudentTestRecord() throws Exception {

        StudentTestRecordDTO updated = sample();
        updated.setMarks(95);

        when(service.updateStudentTestRecord(eq(1L), any(StudentTestRecordDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(
                        put("/api/exams/student-records/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marks").value(95));
    }

    @Test
    @WithMockUser
    void testDeleteStudentTestRecord() throws Exception {

        doNothing().when(service).deleteStudentTestRecord(1L);

        mockMvc.perform(delete("/api/exams/student-records/1")).andExpect(status().isNoContent());

        verify(service, times(1)).deleteStudentTestRecord(1L);
    }
}
