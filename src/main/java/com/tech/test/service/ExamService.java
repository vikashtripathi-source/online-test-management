package com.tech.test.service;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.enums.Branch;
import java.util.List;

public interface ExamService {

    QuestionDTO addQuestion(QuestionDTO questionDTO);

    void deleteQuestion(Long id);

    List<QuestionDTO> addAllQuestions(List<QuestionDTO> questionDTOs);

    List<QuestionDTO> getAllQuestions();

    TestResultResponse submitTest(SubmitTestRequest request);

    StudentTestRecordDTO saveStudentTestRecord(StudentTestRecordDTO recordDTO);

    StudentTestRecordDTO updateStudentTestRecord(Long id, StudentTestRecordDTO recordDTO);

    void deleteStudentTestRecord(Long id);

    List<StudentTestRecordDTO> getRecordsByBranch(Branch branch);

    List<QuestionDTO> getQuestionsByBranch(String branch);

    TestResultResponse getResult(Long studentId);
}
