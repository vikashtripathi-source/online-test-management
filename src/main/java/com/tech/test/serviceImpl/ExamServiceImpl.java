package com.tech.test.serviceImpl;

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
import com.tech.test.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements com.tech.test.service.ExamService {

    private final QuestionRepository questionRepo;
    private final StudentAnswerRepository answerRepo;
    private final KafkaProducerService kafkaProducerService;
    private final StudentTestRecordRepository studentTestRecordRepository;


    public Question addQuestion(Question q) {
        Question saved = questionRepo.save(q);
        kafkaProducerService.sendQuestionAdded(saved);
        return saved;
    }

    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
        kafkaProducerService.sendQuestionDeleted(id);
    }

    public List<Question> addAllQuestions(List<Question> questions) {
        return questionRepo.saveAll(questions);
    }

    public List<QuestionDTO> getAllQuestions() {

        List<Question> questions = questionRepo.findAll();

        return questions.stream().map(q -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(q.getId());
            dto.setQuestion(q.getQuestion());
            dto.setOptionA(q.getOptionA());
            dto.setOptionB(q.getOptionB());
            dto.setOptionC(q.getOptionC());
            dto.setOptionD(q.getOptionD());
            return dto;
        }).toList();
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

    public StudentTestRecord saveStudentTestRecord(StudentTestRecord record) {
        return studentTestRecordRepository.save(record);
    }

    public StudentTestRecord updateStudentTestRecord(Long id, StudentTestRecord recordDetails) {
        Optional<StudentTestRecord> optionalRecord = studentTestRecordRepository.findById(id);
        if (optionalRecord.isPresent()) {
            StudentTestRecord record = optionalRecord.get();
            record.setRollNumber(recordDetails.getRollNumber());
            record.setBranch(recordDetails.getBranch());
            record.setMarks(recordDetails.getMarks());
            record.setStudentId(recordDetails.getStudentId());
            StudentTestRecord saved = studentTestRecordRepository.save(record);
            kafkaProducerService.sendStudentRecordUpdated(saved);
            return saved;
        } else {
            throw new RuntimeException("StudentTestRecord not found with id " + id);
        }
    }

    public void deleteStudentTestRecord(Long id) {
        studentTestRecordRepository.deleteById(id);
        kafkaProducerService.sendStudentRecordDeleted(id);
    }

    public List<StudentTestRecord> getRecordsByBranch(Branch branch) {
        return studentTestRecordRepository.findByBranch(branch);
    }
}