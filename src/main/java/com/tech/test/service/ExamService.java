package com.tech.test.service;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final QuestionRepository questionRepo;
    private final StudentAnswerRepository answerRepo;
    private final KafkaProducerService kafkaProducerService;


    public Question addQuestion(Question q) {
        return questionRepo.save(q);
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
}