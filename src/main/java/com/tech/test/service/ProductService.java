package com.tech.test.service;

import com.tech.test.dto.ProductDTO;
import java.util.List;

public interface ProductService {

    ProductDTO create(ProductDTO dto);

    List<ProductDTO> getAll();

    ProductDTO getById(Long id);

    List<ProductDTO> getByBranch(String branch);

    ProductDTO update(Long id, ProductDTO dto);

    void delete(Long id);
}
