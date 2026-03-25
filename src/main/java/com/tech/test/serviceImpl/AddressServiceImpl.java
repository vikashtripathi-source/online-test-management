package com.tech.test.serviceImpl;

import com.tech.test.dto.AddressDTO;
import com.tech.test.entity.Address;
import com.tech.test.enums.AddressType;
import com.tech.test.exception.AddressException;
import com.tech.test.exception.InvalidDataException;
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
        try {
            if (addressDTO == null) {
                throw new InvalidDataException("Address DTO cannot be null");
            }
            Address address = addressMapper.toEntity(addressDTO);
            Address savedAddress = addressRepository.save(address);
            return addressMapper.toDTO(savedAddress);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AddressException("Failed to create address: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        try {
            return addressRepository.findAll().stream()
                    .map(addressMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AddressException("Failed to retrieve all addresses: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<AddressDTO> getAddressById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Address ID must be a positive number");
            }
            return addressRepository.findById(id)
                    .map(addressMapper::toDTO);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AddressException("Failed to retrieve address with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Address ID must be a positive number");
            }
            if (addressDTO == null) {
                throw new InvalidDataException("Address DTO cannot be null");
            }
            Optional<Address> optionalAddress = addressRepository.findById(id);
            if (optionalAddress.isPresent()) {
                Address updatedAddress = addressMapper.toEntity(addressDTO);
                updatedAddress.setId(id);
                Address saved = addressRepository.save(updatedAddress);
                return addressMapper.toDTO(saved);
            } else {
                throw new AddressException("Address not found with ID: " + id);
            }
        } catch (AddressException | InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AddressException("Failed to update address with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAddress(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Address ID must be a positive number");
            }
            if (!addressRepository.existsById(id)) {
                throw new AddressException("Address not found with ID: " + id);
            }
            addressRepository.deleteById(id);
        } catch (AddressException | InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AddressException("Failed to delete address with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<AddressDTO> getAddressesByType(AddressType addressType) {
        try {
            if (addressType == null) {
                throw new InvalidDataException("Address type cannot be null");
            }
            return addressRepository.findByAddressType(addressType).stream()
                    .map(addressMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AddressException("Failed to retrieve addresses by type " + addressType + ": " + e.getMessage(), e);
        }
    }
}
