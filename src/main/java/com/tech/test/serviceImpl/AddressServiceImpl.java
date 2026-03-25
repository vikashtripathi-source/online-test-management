package com.tech.test.serviceImpl;

import com.tech.test.dto.AddressDTO;
import com.tech.test.entity.Address;
import com.tech.test.enums.AddressType;
import com.tech.test.mapper.AddressMapper;
import com.tech.test.repository.AddressRepository;
import com.tech.test.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address address = addressMapper.toEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AddressDTO> getAddressById(Long id) {
        return addressRepository.findById(id)
                .map(addressMapper::toDTO);
    }

    @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            // Update fields from DTO
            Address updatedAddress = addressMapper.toEntity(addressDTO);
            updatedAddress.setId(id); // Preserve the ID
            Address saved = addressRepository.save(updatedAddress);
            return addressMapper.toDTO(saved);
        } else {
            throw new RuntimeException("Address not found with id " + id);
        }
    }

    @Override
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with id " + id);
        }
        addressRepository.deleteById(id);
    }

    @Override
    public List<AddressDTO> getAddressesByType(AddressType addressType) {
        return addressRepository.findByAddressType(addressType).stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }
}
