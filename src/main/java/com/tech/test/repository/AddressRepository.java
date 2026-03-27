package com.tech.test.repository;

import com.tech.test.entity.Address;
import com.tech.test.enums.AddressType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByAddressType(AddressType addressType);

    Optional<Address> findByStudentId(Long studentId);

    List<Address> findAllByStudentId(Long studentId);
}
