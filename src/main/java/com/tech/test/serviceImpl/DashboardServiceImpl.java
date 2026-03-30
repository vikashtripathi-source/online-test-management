package com.tech.test.serviceImpl;

import com.tech.test.dto.DashboardDTO;
import com.tech.test.entity.Order;
import com.tech.test.entity.Student;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.StudentRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.service.DashboardService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final StudentTestRecordRepository recordRepository;
    private final StudentRepository studentRepository;

    @Override
    public DashboardDTO getDashboard(Long studentId) {

        DashboardDTO dto = new DashboardDTO();
        dto.setStudentId(studentId);

        // Get student information
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            dto.setStudentName(student.getName());
            dto.setBranch(student.getBranch());
        }

        // Get orders
        List<Order> orders = orderRepository.findByStudentId(studentId);
        dto.setTotalOrders(orders.size());

        // Get recent orders (last 5)
        List<String> recentOrders =
                orders.stream()
                        .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                        .limit(5)
                        .map(
                                order ->
                                        order.getProductName()
                                                + " - "
                                                + order.getQuantity()
                                                + " units")
                        .collect(Collectors.toList());
        dto.setRecentOrders(recentOrders);

        // Get test records
        List<StudentTestRecord> records = recordRepository.findByStudentId(studentId);
        dto.setTotalTests(records.size());

        // Calculate average marks
        double avg = records.stream().mapToInt(StudentTestRecord::getMarks).average().orElse(0);
        dto.setAverageMarks(avg);

        // Get recent tests (last 5)
        List<String> recentTests =
                records.stream()
                        .sorted((r1, r2) -> r2.getId().compareTo(r1.getId()))
                        .limit(5)
                        .map(
                                record ->
                                        "Test ID: "
                                                + record.getId()
                                                + " - Marks: "
                                                + record.getMarks())
                        .collect(Collectors.toList());
        dto.setRecentTests(recentTests);

        // Determine performance status
        dto.setPerformanceStatus(calculatePerformanceStatus(avg));

        return dto;
    }

    private String calculatePerformanceStatus(double averageMarks) {
        if (averageMarks >= 90) {
            return "Excellent";
        } else if (averageMarks >= 75) {
            return "Good";
        } else if (averageMarks >= 60) {
            return "Average";
        } else {
            return "Poor";
        }
    }
}
