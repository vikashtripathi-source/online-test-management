package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Inventory;
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
        Inventory inventory = inventoryRepository.findByProductName(orderDTO.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found in inventory"));

        if (inventory.getStockQuantity() >= orderDTO.getQuantity()) {
            inventory.setStockQuantity(inventory.getStockQuantity() - orderDTO.getQuantity());
            inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + orderDTO.getProductName());
        }
    }
}
