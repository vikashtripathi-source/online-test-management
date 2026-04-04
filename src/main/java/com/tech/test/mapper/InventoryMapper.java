package com.tech.test.mapper;

import com.tech.test.dto.InventoryDTO;
import com.tech.test.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    InventoryDTO toDTO(Inventory inventory);

    Inventory toEntity(InventoryDTO inventoryDTO);

    java.util.List<InventoryDTO> toDTOList(java.util.List<Inventory> inventories);

    java.util.List<Inventory> toEntityList(java.util.List<InventoryDTO> inventoryDTOs);
}
