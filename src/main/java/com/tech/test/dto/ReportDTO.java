package com.tech.test.dto;

import com.tech.test.enums.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private String studentName;
    private Branch branch;
    private String orderDetail;
    private String address;
    private LocalDateTime orderCreateDate;
    private String studentMobileNumber;
}
