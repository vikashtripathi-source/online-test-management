package com.tech.test.dto;

import com.tech.test.enums.Branch;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
