package com.tech.test.dto;


import lombok.Data;
import java.util.List;

@Data
public class SubmitTestRequest {
    private Long studentId;
    private List<AnswerRequest> answers;


}
