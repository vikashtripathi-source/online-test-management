package com.tech.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tech.test.dto.AddressDTO;
import com.tech.test.entity.Address;
import com.tech.test.mapper.AddressMapper;
import com.tech.test.repository.AddressRepository;
import com.tech.test.serviceImpl.AddressServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock private AddressRepository addressRepository;
    @Mock private AddressMapper addressMapper;

    @InjectMocks private AddressServiceImpl addressService;

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
    void testCreateAddress_Success() {
        AddressDTO addressDTO = sampleAddressDTO();
        Address address = new Address();
        Address savedAddress = new Address();
        savedAddress.setId(1L);
        savedAddress.setStreetAddress(addressDTO.getStreetAddress());

        AddressDTO resultDTO = new AddressDTO();
        resultDTO.setId(1L);
        resultDTO.setStreetAddress(addressDTO.getStreetAddress());

        when(addressMapper.toEntity(addressDTO)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(addressMapper.toDTO(savedAddress)).thenReturn(resultDTO);

        AddressDTO result = addressService.createAddress(addressDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getStreetAddress());
    }

    @Test
    void testGetAllAddresses_Success() {
        Address address = new Address();
        address.setId(1L);
        address.setStreetAddress("123 Main St");

        AddressDTO addressDTO = sampleAddressDTO();
        addressDTO.setId(1L);

        List<Address> addresses = Arrays.asList(address);

        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.toDTO(address)).thenReturn(addressDTO);

        List<AddressDTO> result = addressService.getAllAddresses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetAddressById_Success() {
        Address address = new Address();
        address.setId(1L);
        address.setStreetAddress("123 Main St");

        AddressDTO addressDTO = sampleAddressDTO();
        addressDTO.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDTO(address)).thenReturn(addressDTO);

        Optional<AddressDTO> result = addressService.getAddressById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetAddressById_NotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AddressDTO> result = addressService.getAddressById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateAddress_Success() {
        AddressDTO addressDTO = sampleAddressDTO();
        Address existingAddress = new Address();
        existingAddress.setId(1L);
        Address updatedAddress = new Address();
        updatedAddress.setId(1L);
        updatedAddress.setStreetAddress("456 Oak Ave");

        AddressDTO resultDTO = new AddressDTO();
        resultDTO.setId(1L);
        resultDTO.setStreetAddress("456 Oak Ave");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);
        when(addressMapper.toDTO(updatedAddress)).thenReturn(resultDTO);

        AddressDTO result = addressService.updateAddress(1L, addressDTO);

        assertNotNull(result);
        assertEquals("456 Oak Ave", result.getStreetAddress());
    }

    @Test
    void testUpdateAddress_NotFound() {
        AddressDTO addressDTO = sampleAddressDTO();

        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                com.tech.test.exception.AddressException.class,
                () -> addressService.updateAddress(1L, addressDTO));
    }

    @Test
    void testDeleteAddress_Success() {
        when(addressRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(1L);

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAddress_NotFound() {
        when(addressRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                com.tech.test.exception.AddressException.class,
                () -> addressService.deleteAddress(1L));
    }
}
