package com.tech.test.mapper;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTO(Order order);

    Order toEntity(OrderDTO orderDTO);

    java.util.List<OrderDTO> toDTOList(java.util.List<Order> orders);

    java.util.List<Order> toEntityList(java.util.List<OrderDTO> orderDTOs);
}
