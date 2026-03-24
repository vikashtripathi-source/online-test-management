package com.tech.test.service;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private StudentAnswerRepository answerRepo;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ExamService examService;

    @Test
    public void testAddQuestion() {
        Question question = new Question(1L, "What is 2+2?", "3", "4", "5", "6", "B");
        when(questionRepo.save(question)).thenReturn(question);

        Question result = examService.addQuestion(question);

        assertEquals(question, result);
        verify(questionRepo, times(1)).save(question);
    }

    @Test
    public void testAddAllQuestions() {
        Question q1 = new Question(1L, "Q1", "A", "B", "C", "D", "A");
        Question q2 = new Question(2L, "Q2", "A", "B", "C", "D", "B");
        List<Question> questions = Arrays.asList(q1, q2);
        when(questionRepo.saveAll(questions)).thenReturn(questions);

        List<Question> result = examService.addAllQuestions(questions);

        assertEquals(questions, result);
        verify(questionRepo, times(1)).saveAll(questions);
    }

    @Test
    public void testGetAllQuestions() {
        Question q1 = new Question(1L, "Q1", "A", "B", "C", "D", "A");
        Question q2 = new Question(2L, "Q2", "A", "B", "C", "D", "B");
        List<Question> questions = Arrays.asList(q1, q2);
        when(questionRepo.findAll()).thenReturn(questions);

        List<QuestionDTO> result = examService.getAllQuestions();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Q1", result.get(0).getQuestion());
        assertEquals("A", result.get(0).getOptionA());
        assertEquals("B", result.get(0).getOptionB());
        assertEquals("C", result.get(0).getOptionC());
        assertEquals("D", result.get(0).getOptionD());
        assertEquals(2L, result.get(1).getId());
        verify(questionRepo, times(1)).findAll();
    }

    @Test
    public void testSubmitTest() {
        Question q1 = new Question(1L, "Q1", "A", "B", "C", "D", "A");
        Question q2 = new Question(2L, "Q2", "A", "B", "C", "D", "B");

        AnswerRequest ans1 = new AnswerRequest();
        ans1.setQuestionId(1L);
        ans1.setSelectedAnswer("A");

        AnswerRequest ans2 = new AnswerRequest();
        ans2.setQuestionId(2L);
        ans2.setSelectedAnswer("C"); // Wrong

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(Arrays.asList(ans1, ans2));

        when(questionRepo.findById(1L)).thenReturn(Optional.of(q1));
        when(questionRepo.findById(2L)).thenReturn(Optional.of(q2));
        when(answerRepo.save(any(StudentAnswer.class))).thenReturn(new StudentAnswer());
        doNothing().when(kafkaProducerService).sendTestSubmission(any(SubmitTestRequest.class));

        TestResultResponse result = examService.submitTest(request);

        assertEquals(1L, result.getStudentId());
        assertEquals(1, result.getScore()); // Only one correct
        assertEquals(2, result.getTotalQuestions());

        verify(questionRepo, times(1)).findById(1L);
        verify(questionRepo, times(1)).findById(2L);
        verify(answerRepo, times(2)).save(any(StudentAnswer.class));
        verify(kafkaProducerService, times(1)).sendTestSubmission(request);
    }

    @Test
    public void testSubmitTestWithNonExistentQuestion() {
        AnswerRequest ans1 = new AnswerRequest();
        ans1.setQuestionId(1L);
        ans1.setSelectedAnswer("A");

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(Arrays.asList(ans1));

        when(questionRepo.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(kafkaProducerService).sendTestSubmission(any(SubmitTestRequest.class));

        TestResultResponse result = examService.submitTest(request);

        assertEquals(1L, result.getStudentId());
        assertEquals(0, result.getScore()); // No correct answers since question not found
        assertEquals(1, result.getTotalQuestions());

        verify(questionRepo, times(1)).findById(1L);
        verify(answerRepo, never()).save(any(StudentAnswer.class)); // No save because question not found
        verify(kafkaProducerService, times(1)).sendTestSubmission(request);
    }
}
