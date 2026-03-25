package com.tech.test.repository;

import com.tech.test.entity.Address;
import com.tech.test.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByAddressType(AddressType addressType);
}

