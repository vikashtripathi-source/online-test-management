package com.tech.test.service;

import com.tech.test.dto.AddressDTO;
import com.tech.test.enums.AddressType;
import java.util.List;
import java.util.Optional;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    Optional<AddressDTO> getAddressById(Long id);

    AddressDTO updateAddress(Long id, AddressDTO addressDTO);

    void deleteAddress(Long id);

    List<AddressDTO> getAddressesByType(AddressType addressType);

    List<AddressDTO> getByStudent(Long studentId);
}
