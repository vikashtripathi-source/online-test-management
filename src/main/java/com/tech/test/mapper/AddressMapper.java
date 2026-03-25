package com.tech.test.mapper;

import com.tech.test.dto.AddressDTO;
import com.tech.test.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO toDTO(Address address);

    Address toEntity(AddressDTO addressDTO);

    // For collections
    java.util.List<AddressDTO> toDTOList(java.util.List<Address> addresses);
    java.util.List<Address> toEntityList(java.util.List<AddressDTO> addressDTOs);
}

