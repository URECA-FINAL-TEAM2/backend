package com.beautymeongdang.domain.user.repository;

import com.beautymeongdang.domain.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
