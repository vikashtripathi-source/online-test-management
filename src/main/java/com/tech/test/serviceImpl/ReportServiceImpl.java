package com.tech.test.serviceImpl;

import com.tech.test.dto.ReportDTO;
import com.tech.test.entity.Address;
import com.tech.test.entity.Order;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.repository.AddressRepository;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StudentTestRecordRepository studentRepo;
    private final AddressRepository addressRepo;
    private final OrderRepository orderRepo;

    @Override
    public List<ReportDTO> generateReportData() {

        List<ReportDTO> reportList = new ArrayList<>();

        List<StudentTestRecord> students = studentRepo.findAll();

        for (StudentTestRecord student : students) {

            Address address = addressRepo.findByStudentId(student.getStudentId()).orElse(null);

            List<Order> orders = orderRepo.findByStudentId(student.getStudentId());

            if (orders.isEmpty()) {

                ReportDTO dto = new ReportDTO();

                dto.setStudentName(student.getRollNumber());
                dto.setBranch(student.getBranch());
                dto.setOrderDetail("No Order");
                dto.setAddress(address != null ? address.getStreetAddress() + "," + address.getCity() : "No Address");
                dto.setStudentMobileNumber(address != null ? address.getPhoneNumber() : "No Phone");

                reportList.add(dto);
            } else {

                for (Order order : orders) {

                    ReportDTO dto = new ReportDTO();

                    dto.setStudentName(student.getRollNumber());
                    dto.setBranch(student.getBranch());
                    dto.setOrderDetail(order.getProductName());
                    dto.setAddress(address != null ? address.getStreetAddress() + "," + address.getCity() : "No Address");
                    dto.setOrderCreateDate(order.getCreatedDate());
                    dto.setStudentMobileNumber(address != null ? address.getPhoneNumber() : "No Phone");

                    reportList.add(dto);
                }
            }
        }

        return reportList;
    }
}
