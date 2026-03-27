package com.tech.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tech.test.dto.DashboardDTO;
import com.tech.test.entity.Order;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.StudentTestRecordRepository;
import com.tech.test.serviceImpl.DashboardServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private OrderRepository orderRepository;

    @Mock private StudentTestRecordRepository studentTestRecordRepository;

    @InjectMocks private DashboardServiceImpl dashboardService;

    @Test
    void testGetDashboard_Success() {
        List<Order> orders = Arrays.asList();
        Order order = new Order();
        order.setStudentId(1L);
        orders.add(order);

        StudentTestRecord record = new StudentTestRecord();
        record.setId(1L);
        record.setBranch(com.tech.test.enums.Branch.CSE);
        record.setMarks(85);
        List<StudentTestRecord> records = Arrays.asList(record);

        DashboardDTO expected = new DashboardDTO();
        expected.setTotalOrders(1);
        expected.setTotalTests(1);
        expected.setAverageMarks(85.0);
        expected.setBranch("CSE");

        when(orderRepository.findByStudentId(1L)).thenReturn(orders);
        when(studentTestRecordRepository.findByStudentId(1L)).thenReturn(records);

        DashboardDTO result = dashboardService.getDashboard(1L);

        assertEquals(expected.getTotalOrders(), result.getTotalOrders());
        assertEquals(expected.getTotalTests(), result.getTotalTests());
        assertEquals(expected.getAverageMarks(), result.getAverageMarks(), 0.01);
        assertEquals(expected.getBranch(), result.getBranch());
    }

    @Test
    void testGetDashboard_NoOrders() {
        when(orderRepository.findByStudentId(1L)).thenReturn(Arrays.asList());
        when(studentTestRecordRepository.findByStudentId(1L)).thenReturn(Arrays.asList());

        DashboardDTO result = dashboardService.getDashboard(1L);

        assertEquals(0, result.getTotalOrders());
        assertEquals(0, result.getTotalTests());
        assertEquals(0.0, result.getAverageMarks());
        assertEquals("N/A", result.getBranch());
    }

    @Test
    void testGetDashboard_NoTestRecords() {
        when(orderRepository.findByStudentId(1L)).thenReturn(Arrays.asList());
        when(studentTestRecordRepository.findByStudentId(1L)).thenReturn(Arrays.asList());

        DashboardDTO result = dashboardService.getDashboard(1L);

        assertEquals(0, result.getTotalOrders());
        assertEquals(0, result.getTotalTests());
        assertEquals(0.0, result.getAverageMarks());
        assertEquals("N/A", result.getBranch());
    }
}
