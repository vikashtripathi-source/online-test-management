package com.tech.test.serviceImpl;

import com.tech.test.entity.Inventory;
import com.tech.test.entity.Order;
import com.tech.test.repository.InventoryRepository;
import com.tech.test.service.InventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void updateInventory(Order order) {
        Inventory inventory = inventoryRepository.findByProductName(order.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found in inventory"));

        if (inventory.getStockQuantity() >= order.getQuantity()) {
            inventory.setStockQuantity(inventory.getStockQuantity() - order.getQuantity());
            inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + order.getProductName());
        }
    }
}
