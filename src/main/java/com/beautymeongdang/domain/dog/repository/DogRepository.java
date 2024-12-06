package com.beautymeongdang.domain.dog.repository;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Customer customerId);
    Long countByCustomerIdAndIsDeletedFalse(Customer customer);
}