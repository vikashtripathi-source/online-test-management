package com.tech.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamService service;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentTestRecord sample() {
        return new StudentTestRecord(1L,"12345",
                Branch.CSE,85,1L);
    }

    @Test
    void testSaveStudentTestRecord() throws Exception {

        when(service.saveStudentTestRecord(any()))
                .thenReturn(sample());

        mockMvc.perform(post("/api/exams/student-records")   // ⭐ FIXED
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.branch").value("CSE"));

        verify(service).saveStudentTestRecord(any());
    }

    @Test
    void testGetRecordsByBranch() throws Exception {

        when(service.getRecordsByBranch(Branch.CSE))
                .thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/exams/student-records/branch/CSE")) // ⭐ FIXED
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].branch").value("CSE"));

        verify(service).getRecordsByBranch(Branch.CSE);
    }

    @Test
    void testUpdateStudentTestRecord() throws Exception {

        StudentTestRecord updated =
                new StudentTestRecord(1L,"54321",
                        Branch.EC,90,2L);

        when(service.updateStudentTestRecord(eq(1L), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/exams/student-records/1") // ⭐ FIXED
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branch").value("EC"));

        verify(service).updateStudentTestRecord(eq(1L), any());
    }

    @Test
    void testDeleteStudentTestRecord() throws Exception {

        doNothing().when(service).deleteStudentTestRecord(1L);

        mockMvc.perform(delete("/api/exams/student-records/1")) // ⭐ FIXED
                .andExpect(status().isNoContent());

        verify(service).deleteStudentTestRecord(1L);
    }
}