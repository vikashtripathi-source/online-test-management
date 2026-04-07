package com.tech.test.mapper;

import com.tech.test.dto.OrderDTO;
import com.tech.test.dto.OrderItemDTO;
import com.tech.test.entity.Order;
import com.tech.test.entity.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "mapOrderItems")
    OrderDTO toDTO(Order order);

    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderDTO orderDTO);

    OrderItemDTO toOrderItemDTO(OrderItem orderItem);

    @Named("mapOrderItems")
    default List<OrderItemDTO> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        return orderItems.stream().map(this::toOrderItemDTO).toList();
    }

    List<OrderDTO> toDTOList(List<Order> orders);

    List<Order> toEntityList(List<OrderDTO> orderDTOs);
}
