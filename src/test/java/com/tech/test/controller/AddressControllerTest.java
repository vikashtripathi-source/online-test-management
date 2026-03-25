package com.tech.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.entity.Address;
import com.tech.test.enums.AddressType;
import com.tech.test.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private Address homeAddress;
    private Address collegeAddress;

    @BeforeEach
    public void setUp() {
        // Create test addresses
        homeAddress = new Address();
        homeAddress.setId(1L);
        homeAddress.setStreetAddress("123 Home Street");
        homeAddress.setCity("New York");
        homeAddress.setState("NY");
        homeAddress.setZipCode("10001");
        homeAddress.setCountry("USA");
        homeAddress.setAddressType(AddressType.HOME);
        homeAddress.setPhoneNumber("123-456-7890");
        homeAddress.setEmail("home@example.com");

        collegeAddress = new Address();
        collegeAddress.setId(2L);
        collegeAddress.setStreetAddress("456 College Avenue");
        collegeAddress.setCity("Boston");
        collegeAddress.setState("MA");
        collegeAddress.setZipCode("02108");
        collegeAddress.setCountry("USA");
        collegeAddress.setAddressType(AddressType.COLLEGE);
        collegeAddress.setPhoneNumber("987-654-3210");
        collegeAddress.setEmail("college@example.com");
    }

    @Test
    public void testCreateAddress() throws Exception {
        when(addressService.createAddress(any(Address.class))).thenReturn(homeAddress);

        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(homeAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.streetAddress").value("123 Home Street"))
                .andExpect(jsonPath("$.addressType").value("HOME"));
    }

    @Test
    public void testGetAllAddresses() throws Exception {
        List<Address> addresses = Arrays.asList(homeAddress, collegeAddress);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].addressType").value("HOME"))
                .andExpect(jsonPath("$[1].addressType").value("COLLEGE"));
    }

    @Test
    public void testGetAddressById() throws Exception {
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(homeAddress));

        mockMvc.perform(get("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.streetAddress").value("123 Home Street"));
    }

    @Test
    public void testGetAddressById_NotFound() throws Exception {
        when(addressService.getAddressById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/addresses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAddress() throws Exception {
        Address updatedAddress = new Address();
        updatedAddress.setId(1L);
        updatedAddress.setStreetAddress("456 Updated Street");
        updatedAddress.setCity("Los Angeles");
        updatedAddress.setState("CA");
        updatedAddress.setZipCode("90001");
        updatedAddress.setCountry("USA");
        updatedAddress.setAddressType(AddressType.HOME);
        updatedAddress.setPhoneNumber("111-222-3333");
        updatedAddress.setEmail("updated@example.com");

        when(addressService.updateAddress(eq(1L), any(Address.class))).thenReturn(updatedAddress);

        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streetAddress").value("456 Updated Street"))
                .andExpect(jsonPath("$.city").value("Los Angeles"));
    }

    @Test
    public void testDeleteAddress() throws Exception {
        doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(delete("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(1L);
    }

    @Test
    public void testGetAddressesByType() throws Exception {
        List<Address> homeAddresses = Arrays.asList(homeAddress);
        when(addressService.getAddressesByType(AddressType.HOME)).thenReturn(homeAddresses);

        mockMvc.perform(get("/api/addresses/type/HOME")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].addressType").value("HOME"));
    }

    @Test
    public void testGetAddressesByType_College() throws Exception {
        List<Address> collegeAddresses = Arrays.asList(collegeAddress);
        when(addressService.getAddressesByType(AddressType.COLLEGE)).thenReturn(collegeAddresses);

        mockMvc.perform(get("/api/addresses/type/COLLEGE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].addressType").value("COLLEGE"));
    }
}

