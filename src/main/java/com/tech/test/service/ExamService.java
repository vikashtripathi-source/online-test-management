package com.tech.test.service;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;

import java.util.List;

public interface ExamService {

    Question addQuestion(Question q);

    void deleteQuestion(Long id);

    List<Question> addAllQuestions(List<Question> questions);

    List<QuestionDTO> getAllQuestions();

    TestResultResponse submitTest(SubmitTestRequest request);

    StudentTestRecord saveStudentTestRecord(StudentTestRecord record);

    StudentTestRecord updateStudentTestRecord(Long id, StudentTestRecord record);

    void deleteStudentTestRecord(Long id);

    List<StudentTestRecord> getRecordsByBranch(Branch branch);
}
