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
        Question question = questionMapper.toEntity(questionDTO);
        Question saved = questionRepo.save(question);
        kafkaProducerService.sendQuestionAdded(saved);
        return questionMapper.toDTO(saved);
    }

    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
        kafkaProducerService.sendQuestionDeleted(id);
    }

    public List<QuestionDTO> addAllQuestions(List<QuestionDTO> questionDTOs) {
        List<Question> questions = questionMapper.toEntityList(questionDTOs);
        List<Question> savedQuestions = questionRepo.saveAll(questions);
        return questionMapper.toDTOList(savedQuestions);
    }

    public List<QuestionDTO> getAllQuestions() {
        List<Question> questions = questionRepo.findAll();
        return questionMapper.toDTOList(questions);
    }

    public TestResultResponse submitTest(SubmitTestRequest request) {

        int score = 0;
        int total = request.getAnswers().size();

        for (AnswerRequest ans : request.getAnswers()) {

            Question q = questionRepo.findById(ans.getQuestionId()).orElse(null);

            if (q != null) {

                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setStudentId(request.getStudentId());
                studentAnswer.setQuestionId(ans.getQuestionId());
                studentAnswer.setSelectedAnswer(ans.getSelectedAnswer());

                answerRepo.save(studentAnswer);

                if (q.getCorrectAnswer().equalsIgnoreCase(ans.getSelectedAnswer())) {
                    score++;
                }
            }
        }

        TestResultResponse response = new TestResultResponse(request.getStudentId(), score, total);
        kafkaProducerService.sendTestSubmission(request);

        return response;
    }

    public StudentTestRecordDTO saveStudentTestRecord(StudentTestRecordDTO recordDTO) {
        StudentTestRecord record = studentTestRecordMapper.toEntity(recordDTO);
        StudentTestRecord saved = studentTestRecordRepository.save(record);
        return studentTestRecordMapper.toDTO(saved);
    }

    public StudentTestRecordDTO updateStudentTestRecord(Long id, StudentTestRecordDTO recordDTO) {
        Optional<StudentTestRecord> optionalRecord = studentTestRecordRepository.findById(id);
        if (optionalRecord.isPresent()) {
            StudentTestRecord record = optionalRecord.get();
            // Update fields from DTO
            StudentTestRecord updatedRecord = studentTestRecordMapper.toEntity(recordDTO);
            updatedRecord.setId(id); // Preserve the ID
            StudentTestRecord saved = studentTestRecordRepository.save(updatedRecord);
            kafkaProducerService.sendStudentRecordUpdated(saved);
            return studentTestRecordMapper.toDTO(saved);
        } else {
            throw new RuntimeException("StudentTestRecord not found with id " + id);
        }
    }

    public void deleteStudentTestRecord(Long id) {
        studentTestRecordRepository.deleteById(id);
        kafkaProducerService.sendStudentRecordDeleted(id);
    }

    public List<StudentTestRecordDTO> getRecordsByBranch(Branch branch) {
        List<StudentTestRecord> records = studentTestRecordRepository.findByBranch(branch);
        return studentTestRecordMapper.toDTOList(records);
    }
}