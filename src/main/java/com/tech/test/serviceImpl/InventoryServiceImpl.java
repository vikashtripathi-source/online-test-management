package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Inventory;
import com.tech.test.exception.BusinessLogicException;
import com.tech.test.exception.InvalidDataException;
import com.tech.test.exception.InventoryException;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.InventoryRepository;
import com.tech.test.service.InventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, OrderMapper orderMapper) {
        this.inventoryRepository = inventoryRepository;
        this.orderMapper = orderMapper;
    }

    public void updateInventory(OrderDTO orderDTO) {
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            if (orderDTO.getProductName() == null || orderDTO.getProductName().trim().isEmpty()) {
                throw new InvalidDataException("Product name cannot be null or empty");
            }
            if (orderDTO.getQuantity() <= 0) {
                throw new InvalidDataException("Quantity must be a positive number");
            }

            Inventory inventory =
                    inventoryRepository
                            .findByProductName(orderDTO.getProductName())
                            .orElseThrow(
                                    () ->
                                            new InventoryException(
                                                    "Product not found in inventory: "
                                                            + orderDTO.getProductName()));

            if (inventory.getStockQuantity() < orderDTO.getQuantity()) {
                throw new BusinessLogicException(
                        String.format(
                                "Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                orderDTO.getProductName(),
                                inventory.getStockQuantity(),
                                orderDTO.getQuantity()));
            }

            inventory.setStockQuantity(inventory.getStockQuantity() - orderDTO.getQuantity());
            inventoryRepository.save(inventory);
        } catch (InventoryException | BusinessLogicException | InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InventoryException("Failed to update inventory: " + e.getMessage(), e);
        }
    }
}
