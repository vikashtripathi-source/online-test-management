package com.tech.test.serviceImpl;

import com.tech.test.dto.DashboardDTO;
import com.tech.test.entity.Order;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final StudentTestRecordRepository recordRepository;

    @Override
    public DashboardDTO getDashboard(Long studentId) {

        DashboardDTO dto = new DashboardDTO();

        // ✅ Total Orders
        List<Order> orders = orderRepository.findByStudentId(studentId);
        dto.setTotalOrders(orders.size());

        // ✅ Total Tests + Average Marks
        List<StudentTestRecord> records =
                recordRepository.findByStudentId(studentId);

        dto.setTotalTests(records.size());

        double avg = records.stream()
                .mapToInt(StudentTestRecord::getMarks)
                .average()
                .orElse(0);

        dto.setAverageMarks(avg);

        // ✅ Branch (Take from first record if exists)
        if (!records.isEmpty()) {
            dto.setBranch(records.get(0).getBranch().name());
        } else {
            dto.setBranch("N/A");
        }

        return dto;
    }
}