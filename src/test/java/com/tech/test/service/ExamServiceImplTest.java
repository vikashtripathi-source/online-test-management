package com.tech.test.service;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.StudentAnswerDTO;
import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import com.tech.test.mapper.QuestionMapper;
import com.tech.test.mapper.StudentAnswerMapper;
import com.tech.test.mapper.StudentTestRecordMapper;
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
    private QuestionMapper questionMapper;

    @Mock
    private StudentAnswerMapper studentAnswerMapper;

    @Mock
    private StudentTestRecordMapper studentTestRecordMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ExamServiceImpl examService;

    @Test
    void testAddQuestion() {

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(1L);
        questionDTO.setQuestion("What is 2+2?");
        questionDTO.setOptionA("3");
        questionDTO.setOptionB("4");
        questionDTO.setOptionC("5");
        questionDTO.setOptionD("6");

        Question question = new Question(1L,"What is 2+2?","3","4","5","6","B");
        Question savedQuestion = new Question(1L,"What is 2+2?","3","4","5","6","B");

        when(questionMapper.toEntity(questionDTO)).thenReturn(question);
        when(questionRepo.save(question)).thenReturn(savedQuestion);
        when(questionMapper.toDTO(savedQuestion)).thenReturn(questionDTO);

        QuestionDTO result = examService.addQuestion(questionDTO);

        assertEquals(questionDTO,result);
        verify(questionMapper, times(1)).toEntity(questionDTO);
        verify(questionRepo, times(1)).save(question);
        verify(questionMapper, times(1)).toDTO(savedQuestion);
        verify(kafkaProducerService, times(1)).sendQuestionAdded(savedQuestion);
    }

    @Test
    void testAddAllQuestions() {

        QuestionDTO q1DTO = new QuestionDTO();
        q1DTO.setQuestion("Q1");
        q1DTO.setOptionA("A");
        q1DTO.setOptionB("B");
        q1DTO.setOptionC("C");
        q1DTO.setOptionD("D");

        QuestionDTO q2DTO = new QuestionDTO();
        q2DTO.setQuestion("Q2");
        q2DTO.setOptionA("A");
        q2DTO.setOptionB("B");
        q2DTO.setOptionC("C");
        q2DTO.setOptionD("D");

        List<QuestionDTO> questionDTOs = Arrays.asList(q1DTO, q2DTO);

        Question q1 = new Question(1L,"Q1","A","B","C","D","A");
        Question q2 = new Question(2L,"Q2","A","B","C","D","B");
        List<Question> questions = Arrays.asList(q1,q2);

        when(questionMapper.toEntityList(questionDTOs)).thenReturn(questions);
        when(questionRepo.saveAll(questions)).thenReturn(questions);
        when(questionMapper.toDTOList(questions)).thenReturn(questionDTOs);

        List<QuestionDTO> result = examService.addAllQuestions(questionDTOs);

        assertEquals(2,result.size());
        verify(questionMapper, times(1)).toEntityList(questionDTOs);
        verify(questionRepo, times(1)).saveAll(questions);
        verify(questionMapper, times(1)).toDTOList(questions);
    }

    @Test
    void testGetAllQuestions() {

        Question q1 = new Question(1L,"Q1","A","B","C","D","A");
        QuestionDTO q1DTO = new QuestionDTO();
        q1DTO.setId(1L);
        q1DTO.setQuestion("Q1");
        q1DTO.setOptionA("A");
        q1DTO.setOptionB("B");
        q1DTO.setOptionC("C");
        q1DTO.setOptionD("D");

        when(questionRepo.findAll()).thenReturn(List.of(q1));
        when(questionMapper.toDTOList(List.of(q1))).thenReturn(List.of(q1DTO));

        List<QuestionDTO> result = examService.getAllQuestions();

        assertEquals(1,result.size());
        assertEquals("Q1",result.get(0).getQuestion());
        verify(questionRepo, times(1)).findAll();
        verify(questionMapper, times(1)).toDTOList(List.of(q1));
    }

    @Test
    void testSubmitTest() {

        Question q = new Question(1L,"Q1","A","B","C","D","A");

        AnswerRequest ans = new AnswerRequest();
        ans.setQuestionId(1L);
        ans.setSelectedAnswer("A");

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(List.of(ans));

        when(questionRepo.findById(1L)).thenReturn(Optional.of(q));
        when(answerRepo.save(any(StudentAnswer.class))).thenReturn(new StudentAnswer());

        TestResultResponse result = examService.submitTest(request);

        assertEquals(1,result.getScore());
        verify(answerRepo, times(1)).save(any(StudentAnswer.class));
        verify(kafkaProducerService, times(1)).sendTestSubmission(request);
    }

    @Test
    void testSaveStudentTestRecord() {

        StudentTestRecordDTO recordDTO = new StudentTestRecordDTO();
        recordDTO.setId(1L);
        recordDTO.setRollNumber("12345");
        recordDTO.setBranch(Branch.CSE);
        recordDTO.setMarks(85);
        recordDTO.setStudentId(1L);

        StudentTestRecord record = new StudentTestRecord(1L,"12345", Branch.CSE,85,1L);
        StudentTestRecord savedRecord = new StudentTestRecord(1L,"12345", Branch.CSE,85,1L);

        when(studentTestRecordMapper.toEntity(recordDTO)).thenReturn(record);
        when(studentTestRecordRepo.save(record)).thenReturn(savedRecord);
        when(studentTestRecordMapper.toDTO(savedRecord)).thenReturn(recordDTO);

        StudentTestRecordDTO result = examService.saveStudentTestRecord(recordDTO);

        assertEquals(recordDTO,result);
        verify(studentTestRecordMapper, times(1)).toEntity(recordDTO);
        verify(studentTestRecordRepo, times(1)).save(record);
        verify(studentTestRecordMapper, times(1)).toDTO(savedRecord);
    }

    @Test
    void testGetRecordsByBranch() {

        StudentTestRecord record = new StudentTestRecord(1L,"12345",Branch.CSE,85,1L);
        StudentTestRecordDTO recordDTO = new StudentTestRecordDTO();
        recordDTO.setId(1L);
        recordDTO.setRollNumber("12345");
        recordDTO.setBranch(Branch.CSE);
        recordDTO.setMarks(85);
        recordDTO.setStudentId(1L);

        when(studentTestRecordRepo.findByBranch(Branch.CSE)).thenReturn(List.of(record));
        when(studentTestRecordMapper.toDTOList(List.of(record))).thenReturn(List.of(recordDTO));

        List<StudentTestRecordDTO> result = examService.getRecordsByBranch(Branch.CSE);

        assertEquals(1,result.size());
        verify(studentTestRecordRepo, times(1)).findByBranch(Branch.CSE);
        verify(studentTestRecordMapper, times(1)).toDTOList(List.of(record));
    }

    @Test
    void testDeleteQuestion() {

        examService.deleteQuestion(1L);

        verify(questionRepo, times(1)).deleteById(1L);
        verify(kafkaProducerService, times(1)).sendQuestionDeleted(1L);
    }

    @Test
    void testUpdateStudentTestRecord() {

        StudentTestRecordDTO recordDTO = new StudentTestRecordDTO();
        recordDTO.setId(1L);
        recordDTO.setRollNumber("12345");
        recordDTO.setBranch(Branch.CSE);
        recordDTO.setMarks(80);
        recordDTO.setStudentId(1L);

        StudentTestRecord existing = new StudentTestRecord(1L,"12345",Branch.CSE,80,1L);
        StudentTestRecord updatedRecord = new StudentTestRecord(1L,"54321",Branch.IT,90,2L);
        StudentTestRecordDTO updatedRecordDTO = new StudentTestRecordDTO();
        updatedRecordDTO.setId(1L);
        updatedRecordDTO.setRollNumber("54321");
        updatedRecordDTO.setBranch(Branch.IT);
        updatedRecordDTO.setMarks(90);
        updatedRecordDTO.setStudentId(2L);

        when(studentTestRecordRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(studentTestRecordMapper.toEntity(recordDTO)).thenReturn(updatedRecord);
        when(studentTestRecordRepo.save(any(StudentTestRecord.class))).thenReturn(updatedRecord);
        when(studentTestRecordMapper.toDTO(updatedRecord)).thenReturn(updatedRecordDTO);

        StudentTestRecordDTO result = examService.updateStudentTestRecord(1L, recordDTO);

        assertEquals(Branch.IT,result.getBranch());
        verify(studentTestRecordRepo, times(1)).findById(1L);
        verify(studentTestRecordMapper, times(1)).toEntity(recordDTO);
        verify(studentTestRecordRepo, times(1)).save(any(StudentTestRecord.class));
        verify(studentTestRecordMapper, times(1)).toDTO(updatedRecord);
        verify(kafkaProducerService, times(1)).sendStudentRecordUpdated(updatedRecord);
    }

    @Test
    void testDeleteStudentTestRecord() {

        examService.deleteStudentTestRecord(1L);

        verify(studentTestRecordRepo, times(1)).deleteById(1L);
        verify(kafkaProducerService, times(1)).sendStudentRecordDeleted(1L);
    }
}