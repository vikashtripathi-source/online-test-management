package com.tech.test.serviceImpl;

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
import com.tech.test.exception.InvalidDataException;
import com.tech.test.exception.QuestionException;
import com.tech.test.exception.StudentRecordException;
import com.tech.test.exception.TestSubmissionException;
import com.tech.test.mapper.QuestionMapper;
import com.tech.test.mapper.StudentAnswerMapper;
import com.tech.test.mapper.StudentTestRecordMapper;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements com.tech.test.service.ExamService {

    private final QuestionRepository questionRepo;
    private final StudentAnswerRepository answerRepo;
    private final KafkaProducerService kafkaProducerService;
    private final StudentTestRecordRepository studentTestRecordRepository;
    private final QuestionMapper questionMapper;
    private final StudentAnswerMapper studentAnswerMapper;
    private final StudentTestRecordMapper studentTestRecordMapper;

    public QuestionDTO addQuestion(QuestionDTO questionDTO) {
        try {
            if (questionDTO == null) {
                throw new InvalidDataException("Question DTO cannot be null");
            }
            Question question = questionMapper.toEntity(questionDTO);
            Question saved = questionRepo.save(question);
            kafkaProducerService.sendQuestionAdded(saved);
            return questionMapper.toDTO(saved);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new QuestionException("Failed to add question: " + e.getMessage(), e);
        }
    }

    public void deleteQuestion(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Question ID must be a positive number");
            }
            if (!questionRepo.existsById(id)) {
                throw new QuestionException("Question not found with ID: " + id);
            }
            questionRepo.deleteById(id);
            kafkaProducerService.sendQuestionDeleted(id);
        } catch (InvalidDataException | QuestionException e) {
            throw e;
        } catch (Exception e) {
            throw new QuestionException("Failed to delete question with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public List<QuestionDTO> addAllQuestions(List<QuestionDTO> questionDTOs) {
        try {
            if (questionDTOs == null || questionDTOs.isEmpty()) {
                throw new InvalidDataException("Question list cannot be null or empty");
            }
            List<Question> questions = questionMapper.toEntityList(questionDTOs);
            List<Question> savedQuestions = questionRepo.saveAll(questions);
            return questionMapper.toDTOList(savedQuestions);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new QuestionException("Failed to add all questions: " + e.getMessage(), e);
        }
    }

    public List<QuestionDTO> getAllQuestions() {
        try {
            List<Question> questions = questionRepo.findAll();
            return questionMapper.toDTOList(questions);
        } catch (Exception e) {
            throw new QuestionException("Failed to retrieve all questions: " + e.getMessage(), e);
        }
    }

    public TestResultResponse submitTest(SubmitTestRequest request) {
        try {
            if (request == null) {
                throw new TestSubmissionException("Submit test request cannot be null");
            }
            if (request.getStudentId() == null || request.getStudentId() <= 0) {
                throw new TestSubmissionException("Student ID must be a positive number");
            }
            if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
                throw new TestSubmissionException("Answers list cannot be null or empty");
            }

            int score = 0;
            int total = request.getAnswers().size();

            for (AnswerRequest ans : request.getAnswers()) {
                if (ans.getQuestionId() == null || ans.getQuestionId() <= 0) {
                    throw new TestSubmissionException("Question ID must be a positive number");
                }
                if (ans.getSelectedAnswer() == null || ans.getSelectedAnswer().trim().isEmpty()) {
                    throw new TestSubmissionException("Selected answer cannot be null or empty");
                }

                Optional<Question> questionOpt = questionRepo.findById(ans.getQuestionId());
                if (questionOpt.isEmpty()) {
                    throw new TestSubmissionException("Question not found with ID: " + ans.getQuestionId());
                }

                Question q = questionOpt.get();
                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setStudentId(request.getStudentId());
                studentAnswer.setQuestionId(ans.getQuestionId());
                studentAnswer.setSelectedAnswer(ans.getSelectedAnswer());

                answerRepo.save(studentAnswer);

                if (q.getCorrectAnswer().equalsIgnoreCase(ans.getSelectedAnswer())) {
                    score++;
                }
            }

            TestResultResponse response = new TestResultResponse(request.getStudentId(), score, total);
            kafkaProducerService.sendTestSubmission(request);

            return response;
        } catch (TestSubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to submit test: " + e.getMessage(), e);
        }
    }

    public StudentTestRecordDTO saveStudentTestRecord(StudentTestRecordDTO recordDTO) {
        try {
            if (recordDTO == null) {
                throw new InvalidDataException("Student test record DTO cannot be null");
            }
            StudentTestRecord record = studentTestRecordMapper.toEntity(recordDTO);
            StudentTestRecord saved = studentTestRecordRepository.save(record);
            return studentTestRecordMapper.toDTO(saved);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new StudentRecordException("Failed to save student test record: " + e.getMessage(), e);
        }
    }

    public StudentTestRecordDTO updateStudentTestRecord(Long id, StudentTestRecordDTO recordDTO) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Record ID must be a positive number");
            }
            if (recordDTO == null) {
                throw new InvalidDataException("Student test record DTO cannot be null");
            }
            Optional<StudentTestRecord> optionalRecord = studentTestRecordRepository.findById(id);
            if (optionalRecord.isPresent()) {
                StudentTestRecord updatedRecord = studentTestRecordMapper.toEntity(recordDTO);
                updatedRecord.setId(id);
                StudentTestRecord saved = studentTestRecordRepository.save(updatedRecord);
                kafkaProducerService.sendStudentRecordUpdated(saved);
                return studentTestRecordMapper.toDTO(saved);
            } else {
                throw new StudentRecordException("Student test record not found with ID: " + id);
            }
        } catch (StudentRecordException | InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new StudentRecordException("Failed to update student test record with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public void deleteStudentTestRecord(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Record ID must be a positive number");
            }
            if (!studentTestRecordRepository.existsById(id)) {
                throw new StudentRecordException("Student test record not found with ID: " + id);
            }
            studentTestRecordRepository.deleteById(id);
            kafkaProducerService.sendStudentRecordDeleted(id);
        } catch (StudentRecordException | InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new StudentRecordException("Failed to delete student test record with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public List<StudentTestRecordDTO> getRecordsByBranch(Branch branch) {
        try {
            if (branch == null) {
                throw new InvalidDataException("Branch cannot be null");
            }
            List<StudentTestRecord> records = studentTestRecordRepository.findByBranch(branch);
            return studentTestRecordMapper.toDTOList(records);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new StudentRecordException("Failed to retrieve records by branch " + branch + ": " + e.getMessage(), e);
        }
    }
}