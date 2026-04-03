package com.tech.test.repository;

import com.tech.test.entity.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByStudentId(Long studentId);

    Optional<Cart> findByStudentIdAndProductId(Long studentId, Long productId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.studentId = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :itemId AND c.studentId = :studentId")
    int deleteByIdAndStudentId(@Param("itemId") Long itemId, @Param("studentId") Long studentId);
}
