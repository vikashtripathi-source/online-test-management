package com.tech.test.serviceImpl;

import com.tech.test.dto.ProductDTO;
import com.tech.test.entity.Product;
import com.tech.test.enums.Branch;
import com.tech.test.repository.ProductRepository;
import com.tech.test.service.ProductService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    @Value("${app.upload.dir:./uploads/product-images}")
    private String uploadDir;

    @Override
    public ProductDTO create(ProductDTO dto) {

        Product product = new Product();

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setBranch(dto.getBranch());
        product.setImageUrl(dto.getImageUrl());
        product.setSku(dto.getSku());
        product.setActive(dto.isActive());
        product.setStockQuantity(dto.getStockQuantity());

        Product saved = repo.save(product);

        dto.setId(saved.getId());

        return dto;
    }

    @Override
    public List<ProductDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getById(Long id) {
        Product p = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToDTO(p);
    }

    @Override
    public List<ProductDTO> getByBranch(String branch) {
        Branch branchEnum = Branch.valueOf(branch.toUpperCase());
        return repo.findByBranch(branchEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {

        Product p = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

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

    @Override
    public String uploadProductImage(Long id, MultipartFile image) {
        Product product =
                repo.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Product not found with id: " + id));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = image.getOriginalFilename();
            String fileExtension =
                    originalFilename != null && originalFilename.contains(".")
                            ? originalFilename.substring(originalFilename.lastIndexOf("."))
                            : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/product-images/" + uniqueFilename;
            product.setImageUrl(imageUrl);
            product.setImageFilename(uniqueFilename);
            repo.save(product);

            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getProductImage(Long id) {
        Product product =
                repo.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Product not found with id: " + id));

        if (product.getImageFilename() == null) {
            throw new RuntimeException("No image found for product with id: " + id);
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(product.getImageFilename());

            if (!Files.exists(filePath)) {
                // Log warning and return null instead of throwing exception
                System.err.println("Warning: Image file not found: " + product.getImageFilename());
                return null;
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve image: " + e.getMessage(), e);
        }
    }

    private ProductDTO mapToDTO(Product p) {

        ProductDTO dto = new ProductDTO();

        dto.setId(p.getId());
        dto.setProductName(p.getProductName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setBranch(p.getBranch());
        dto.setImageUrl(p.getImageUrl());
        dto.setImageFilename(p.getImageFilename());
        dto.setStockQuantity(p.getStockQuantity());
        dto.setSku(p.getSku());
        dto.setActive(p.isActive());

        return dto;
    }
}
