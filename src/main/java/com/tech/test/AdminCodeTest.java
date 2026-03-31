package com.tech.test;

public class AdminCodeTest {
    public static void main(String[] args) {
        String adminSecretCode = "admin123";
        String receivedCode = "admin123";

        System.out.println("Expected: " + adminSecretCode);
        System.out.println("Received: " + receivedCode);
        System.out.println("Match: " + adminSecretCode.equals(receivedCode));

        if (!"admin123".equals(receivedCode)) {
            System.out.println("Invalid admin code");
        } else {
            System.out.println("Valid admin code");
        }
    }
}
