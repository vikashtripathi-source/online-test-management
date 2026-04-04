package com.tech.test.service;

import com.tech.test.dto.ProductDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    ProductDTO create(ProductDTO dto);

    List<ProductDTO> getAll();

    ProductDTO getById(Long id);

    List<ProductDTO> getByBranch(String branch);

    ProductDTO update(Long id, ProductDTO dto);

    void delete(Long id);

    String uploadProductImage(Long id, MultipartFile image);

    byte[] getProductImage(Long id);
}
