package com.tech.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
public class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamService examService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddQuestion() throws Exception {
        Question question = new Question(1L, "What is 2+2?", "3", "4", "5", "6", "B");
        when(examService.addQuestion(any(Question.class))).thenReturn(question);

        mockMvc.perform(post("/api/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.question").value("What is 2+2?"));

        verify(examService, times(1)).addQuestion(any(Question.class));
    }

    @Test
    public void testAddAllQuestions() throws Exception {
        Question q1 = new Question(1L, "Q1", "A", "B", "C", "D", "A");
        Question q2 = new Question(2L, "Q2", "A", "B", "C", "D", "B");
        List<Question> questions = Arrays.asList(q1, q2);
        when(examService.addAllQuestions(anyList())).thenReturn(questions);

        mockMvc.perform(post("/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(examService, times(1)).addAllQuestions(anyList());
    }

    @Test
    public void testGetQuestions() throws Exception {
        QuestionDTO q1 = new QuestionDTO();
        q1.setId(1L);
        q1.setQuestion("Q1");
        q1.setOptionA("A");
        q1.setOptionB("B");
        q1.setOptionC("C");
        q1.setOptionD("D");
        List<QuestionDTO> questions = Arrays.asList(q1);
        when(examService.getAllQuestions()).thenReturn(questions);

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].question").value("Q1"));

        verify(examService, times(1)).getAllQuestions();
    }

    @Test
    public void testSubmit() throws Exception {
        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        AnswerRequest answer = new AnswerRequest();
        answer.setQuestionId(1L);
        answer.setSelectedAnswer("A");
        request.setAnswers(Arrays.asList(answer));

        TestResultResponse response = new TestResultResponse(1L, 1, 1);
        when(examService.submitTest(any(SubmitTestRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1L))
                .andExpect(jsonPath("$.score").value(1))
                .andExpect(jsonPath("$.totalQuestions").value(1));

        verify(examService, times(1)).submitTest(any(SubmitTestRequest.class));
    }

    @Test
    public void testSubmitTest() throws Exception {
        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        AnswerRequest answer = new AnswerRequest();
        answer.setQuestionId(1L);
        answer.setSelectedAnswer("A");
        request.setAnswers(Arrays.asList(answer));

        doNothing().when(kafkaProducerService).sendTestSubmission(any(SubmitTestRequest.class));

        mockMvc.perform(post("/api/submit-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Test submitted successfully (processing async)"));

        verify(kafkaProducerService, times(1)).sendTestSubmission(any(SubmitTestRequest.class));
    }
}
