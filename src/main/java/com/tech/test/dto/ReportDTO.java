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

    private String studentName;      // rollNumber from StudentTestRecord
    private Branch branch;           // branch from StudentTestRecord
    private String orderDetail;      // productName from Order
    private String address;          // streetAddress + city from Address
    private LocalDateTime orderCreateDate; // createdDate from Order
    private String studentMobileNumber; // phoneNumber from Address
}
