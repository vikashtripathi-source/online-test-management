package com.tech.test.controller;

import com.tech.test.dto.ReportDTO;
import com.tech.test.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCsv() {

        List<ReportDTO> data = reportService.generateReportData();

        StringBuilder csv = new StringBuilder();

        csv.append("Student Name,Branch,Order Detail,Address,Order Date,Mobile\n");

        for (ReportDTO dto : data) {

            csv.append(dto.getStudentName()).append(",");
            csv.append(dto.getBranch()).append(",");
            csv.append(dto.getOrderDetail()).append(",");
            csv.append(dto.getAddress()).append(",");
            csv.append(dto.getOrderCreateDate()).append(",");
            csv.append(dto.getStudentMobileNumber()).append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes());
    }
}
