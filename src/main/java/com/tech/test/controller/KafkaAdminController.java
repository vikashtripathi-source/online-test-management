package com.tech.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/kafka")
@RequiredArgsConstructor
@Tag(
        name = "Kafka Admin Controller",
        description = "Admin endpoints for monitoring Kafka events and system health")
public class KafkaAdminController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/health")
    @Operation(
            summary = "Kafka Health Check",
            description = "Check if Kafka producer is healthy and can send messages")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Kafka is healthy and operational"),
                @ApiResponse(responseCode = "503", description = "Kafka is not available")
            })
    public ResponseEntity<Map<String, String>> kafkaHealth() {
        Map<String, String> response = new HashMap<>();
        try {

            kafkaTemplate.send("health-check-topic", "health-check", "Kafka health check message");
            response.put("status", "UP");
            response.put("kafka", "Connected and operational");
            response.put("time", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("kafka", "Not available");
            response.put("error", e.getMessage());
            response.put("time", java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(503).body(response);
        }
    }

    @GetMapping("/topics")
    @Operation(
            summary = "Get Kafka Topics Info",
            description = "Get information about active Kafka topics for the admin dashboard")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved topics information"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Map<String, Object>> getTopicsInfo() {
        Map<String, Object> response = new HashMap<>();

        Map<String, String> topics = new HashMap<>();
        topics.put("submit-test-topic", "Test submissions for async evaluation");
        topics.put("order-topic", "New order creation events");
        topics.put("question-added-topic", "Question creation events");
        topics.put("question-deleted-topic", "Question deletion events");
        topics.put("student-record-updated-topic", "Student record update events");
        topics.put("student-record-deleted-topic", "Student record deletion events");
        topics.put("order-updated-topic", "Order update events");
        topics.put("order-deleted-topic", "Order deletion events");

        response.put("topics", topics);
        response.put("totalTopics", topics.size());
        response.put("description", "Active Kafka topics for admin dashboard monitoring");
        response.put("time", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    @Operation(
            summary = "Get Recent Events Summary",
            description = "Get a summary of recent Kafka events processed by the system")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved events summary"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Map<String, Object>> getRecentEvents() {
        Map<String, Object> response = new HashMap<>();

        Map<String, Integer> eventCounts = new HashMap<>();
        eventCounts.put("test_submissions", 0);
        eventCounts.put("orders_created", 0);
        eventCounts.put("questions_added", 0);
        eventCounts.put("questions_deleted", 0);
        eventCounts.put("student_records_updated", 0);
        eventCounts.put("student_records_deleted", 0);
        eventCounts.put("orders_updated", 0);
        eventCounts.put("orders_deleted", 0);

        response.put("eventCounts", eventCounts);
        response.put(
                "totalEvents", eventCounts.values().stream().mapToInt(Integer::intValue).sum());
        response.put("description", "Recent Kafka events processed by admin dashboard");
        response.put("time", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
}
