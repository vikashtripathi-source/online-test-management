package com.tech.test.serviceImpl;

import com.tech.test.dto.ReportDTO;
import com.tech.test.entity.Address;
import com.tech.test.entity.Order;
import com.tech.test.entity.Student;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.repository.AddressRepository;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.StudentRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.service.ReportService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StudentTestRecordRepository studentTestRecordRepo;
    private final AddressRepository addressRepo;
    private final OrderRepository orderRepo;
    private final StudentRepository studentRepo;

    @Override
    public List<ReportDTO> generateReportData() {

        List<ReportDTO> reportList = new ArrayList<>();

        List<StudentTestRecord> studentTestRecords = studentTestRecordRepo.findAll();

        for (StudentTestRecord studentTestRecord : studentTestRecords) {

            Student student = studentRepo.findById(studentTestRecord.getStudentId()).orElse(null);
            if (student == null) continue;

            Address address =
                    addressRepo.findByStudentId(studentTestRecord.getStudentId()).orElse(null);

            List<Order> orders = orderRepo.findByStudentId(studentTestRecord.getStudentId());

            if (orders.isEmpty()) {
                ReportDTO dto = new ReportDTO();

                dto.setStudentName(student.getName());
                dto.setBranch(studentTestRecord.getBranch());
                dto.setOrderDetail("No Order");
                dto.setAddress(
                        address != null
                                ? address.getStreetAddress() + "," + address.getCity()
                                : "No Address");
                dto.setStudentMobileNumber(
                        student.getMobileNumber() != null
                                ? student.getMobileNumber()
                                : (address != null ? address.getPhoneNumber() : "No Phone"));

                reportList.add(dto);
            } else {
                for (Order order : orders) {

                    ReportDTO dto = new ReportDTO();

                    dto.setStudentName(student.getName());
                    dto.setBranch(studentTestRecord.getBranch());
                    dto.setOrderDetail(
                            order.getProductName() + " (Qty: " + order.getQuantity() + ")");
                    dto.setAddress(
                            address != null
                                    ? address.getStreetAddress() + "," + address.getCity()
                                    : "No Address");
                    dto.setOrderCreateDate(order.getCreatedDate());
                    dto.setStudentMobileNumber(
                            student.getMobileNumber() != null
                                    ? student.getMobileNumber()
                                    : (address != null ? address.getPhoneNumber() : "No Phone"));

                    reportList.add(dto);
                }
            }
        }

        return reportList;
    }
}
