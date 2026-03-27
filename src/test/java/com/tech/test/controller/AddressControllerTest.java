package com.tech.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.AddressDTO;
import com.tech.test.service.AddressService;
import com.tech.test.util.JwtUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AddressController.class)
@Import(com.tech.test.config.SecurityConfig.class)
class AddressControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AddressService service;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private com.tech.test.security.CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

    private AddressDTO sampleAddressDTO() {
        AddressDTO dto = new AddressDTO();
        dto.setStreetAddress("123 Main St");
        dto.setCity("New York");
        dto.setState("NY");
        dto.setZipCode("10001");
        dto.setCountry("USA");
        dto.setAddressType(com.tech.test.enums.AddressType.HOME);
        dto.setPhoneNumber("555-1234");
        dto.setEmail("john.doe@example.com");
        dto.setStudentId(1L);
        return dto;
    }

    @Test
    @WithMockUser
    void testCreateAddress_Success() throws Exception {
        AddressDTO addressDTO = sampleAddressDTO();
        AddressDTO savedAddress = new AddressDTO();
        savedAddress.setId(1L);
        savedAddress.setStreetAddress(addressDTO.getStreetAddress());

        when(service.createAddress(any(AddressDTO.class))).thenReturn(savedAddress);

        mockMvc.perform(
                        post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.streetAddress").value("123 Main St"));
    }

    @Test
    @WithMockUser
    void testCreateAddress_ValidationError() throws Exception {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetAddress(""); // Invalid empty street address

        mockMvc.perform(
                        post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetAllAddresses_Success() throws Exception {
        List<AddressDTO> addresses = Arrays.asList(sampleAddressDTO());
        addresses.get(0).setId(1L);

        when(service.getAllAddresses()).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].streetAddress").value("123 Main St"));
    }

    @Test
    @WithMockUser
    void testGetAllAddresses_EmptyList() throws Exception {
        when(service.getAllAddresses()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void testGetAddressById_Success() throws Exception {
        AddressDTO addressDTO = sampleAddressDTO();
        addressDTO.setId(1L);

        when(service.getAddressById(1L)).thenReturn(Optional.of(addressDTO));

        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.streetAddress").value("123 Main St"));
    }

    @Test
    @WithMockUser
    void testGetAddressById_NotFound() throws Exception {
        when(service.getAddressById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/addresses/1")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testUpdateAddress_Success() throws Exception {
        AddressDTO addressDTO = sampleAddressDTO();
        AddressDTO updatedAddress = new AddressDTO();
        updatedAddress.setId(1L);
        updatedAddress.setStreetAddress("456 Oak Ave");

        when(service.updateAddress(eq(1L), any(AddressDTO.class))).thenReturn(updatedAddress);

        mockMvc.perform(
                        put("/api/addresses/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streetAddress").value("456 Oak Ave"));
    }

    @Test
    @WithMockUser
    void testUpdateAddress_NotFound() throws Exception {
        AddressDTO addressDTO = sampleAddressDTO();

        when(service.updateAddress(eq(1L), any(AddressDTO.class)))
                .thenThrow(
                        new com.tech.test.exception.AddressException(
                                "Address not found with ID: 1"));

        mockMvc.perform(
                        put("/api/addresses/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testDeleteAddress_Success() throws Exception {
        doNothing().when(service).deleteAddress(1L);

        mockMvc.perform(delete("/api/addresses/1")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testDeleteAddress_NotFound() throws Exception {
        doThrow(new com.tech.test.exception.AddressException("Address not found with ID: 1"))
                .when(service)
                .deleteAddress(1L);

        mockMvc.perform(delete("/api/addresses/1")).andExpect(status().isInternalServerError());
    }
}
