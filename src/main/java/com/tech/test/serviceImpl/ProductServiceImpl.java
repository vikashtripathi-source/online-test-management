package com.tech.test.serviceImpl;

import com.tech.test.dto.ProductDTO;
import com.tech.test.entity.Product;
import com.tech.test.repository.ProductRepository;
import com.tech.test.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    @Override
    public ProductDTO create(ProductDTO dto) {

        Product product = new Product();

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setBranch(dto.getBranch());
        product.setImageUrl(dto.getImageUrl());

        Product saved = repo.save(product);

        dto.setId(saved.getId());

        return dto;
    }

    @Override
    public List<ProductDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToDTO(p);
    }

    @Override
    public List<ProductDTO> getByBranch(String branch) {

        return repo.findByBranch(branch)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {

        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setProductName(dto.getProductName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setBranch(dto.getBranch());
        p.setImageUrl(dto.getImageUrl());

        repo.save(p);

        return mapToDTO(p);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private ProductDTO mapToDTO(Product p) {

        ProductDTO dto = new ProductDTO();

        dto.setId(p.getId());
        dto.setProductName(p.getProductName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setBranch(p.getBranch());
        dto.setImageUrl(p.getImageUrl());

        return dto;
    }
}