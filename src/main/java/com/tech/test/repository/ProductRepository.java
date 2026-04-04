package com.tech.test.repository;

import com.tech.test.entity.Product;
import com.tech.test.enums.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBranch(Branch branch);
}
