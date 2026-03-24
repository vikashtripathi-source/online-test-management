package com.tech.test.service;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.serviceImpl.ExamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamServiceImplTest {

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private StudentAnswerRepository answerRepo;

    @Mock
    private StudentTestRecordRepository studentTestRecordRepo;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ExamServiceImpl examService;

    @Test
    void testAddQuestion() {

        Question question =
                new Question(1L,"What is 2+2?","3","4","5","6","B");

        when(questionRepo.save(question)).thenReturn(question);

        Question result = examService.addQuestion(question);

        assertEquals(question,result);
        verify(questionRepo).save(question);
        verify(kafkaProducerService).sendQuestionAdded(question);
    }

    @Test
    void testAddAllQuestions() {

        Question q1 =
                new Question(1L,"Q1","A","B","C","D","A");
        Question q2 =
                new Question(2L,"Q2","A","B","C","D","B");

        List<Question> questions = Arrays.asList(q1,q2);

        when(questionRepo.saveAll(questions)).thenReturn(questions);

        List<Question> result = examService.addAllQuestions(questions);

        assertEquals(2,result.size());
        verify(questionRepo).saveAll(questions);
    }

    @Test
    void testGetAllQuestions() {

        Question q1 =
                new Question(1L,"Q1","A","B","C","D","A");

        when(questionRepo.findAll()).thenReturn(List.of(q1));

        List<QuestionDTO> result = examService.getAllQuestions();

        assertEquals(1,result.size());
        assertEquals("Q1",result.get(0).getQuestion());

        verify(questionRepo).findAll();
    }

    @Test
    void testSubmitTest() {

        Question q =
                new Question(1L,"Q1","A","B","C","D","A");

        AnswerRequest ans = new AnswerRequest();
        ans.setQuestionId(1L);
        ans.setSelectedAnswer("A");

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(List.of(ans));

        when(questionRepo.findById(1L)).thenReturn(Optional.of(q));
        when(answerRepo.save(any(StudentAnswer.class)))
                .thenReturn(new StudentAnswer());

        TestResultResponse result = examService.submitTest(request);

        assertEquals(1,result.getScore());
        verify(answerRepo).save(any(StudentAnswer.class));
        verify(kafkaProducerService).sendTestSubmission(request);
    }

    @Test
    void testSaveStudentTestRecord() {

        StudentTestRecord record =
                new StudentTestRecord(1L,"12345", Branch.CSE,85,1L);

        when(studentTestRecordRepo.save(record)).thenReturn(record);

        StudentTestRecord result =
                examService.saveStudentTestRecord(record);

        assertEquals(record,result);
        verify(studentTestRecordRepo).save(record);
    }

    @Test
    void testGetRecordsByBranch() {

        StudentTestRecord record =
                new StudentTestRecord(1L,"12345",Branch.CSE,85,1L);

        when(studentTestRecordRepo.findByBranch(Branch.CSE))
                .thenReturn(List.of(record));

        List<StudentTestRecord> result =
                examService.getRecordsByBranch(Branch.CSE);

        assertEquals(1,result.size());
        verify(studentTestRecordRepo).findByBranch(Branch.CSE);
    }

    @Test
    void testDeleteQuestion() {

        examService.deleteQuestion(1L);

        verify(questionRepo).deleteById(1L);
        verify(kafkaProducerService).sendQuestionDeleted(1L);
    }

    @Test
    void testUpdateStudentTestRecord() {

        StudentTestRecord existing =
                new StudentTestRecord(1L,"12345",Branch.CSE,80,1L);

        StudentTestRecord update =
                new StudentTestRecord(null,"54321",Branch.IT,90,2L);

        when(studentTestRecordRepo.findById(1L))
                .thenReturn(Optional.of(existing));

        when(studentTestRecordRepo.save(any()))
                .thenReturn(update);

        StudentTestRecord result =
                examService.updateStudentTestRecord(1L,update);

        assertEquals(Branch.IT,result.getBranch());
        verify(studentTestRecordRepo).save(any());
        verify(kafkaProducerService).sendStudentRecordUpdated(result);
    }

    @Test
    void testDeleteStudentTestRecord() {

        examService.deleteStudentTestRecord(1L);

        verify(studentTestRecordRepo).deleteById(1L);
        verify(kafkaProducerService).sendStudentRecordDeleted(1L);
    }
}