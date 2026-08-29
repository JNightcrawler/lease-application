package com.leaseapp.repository;

import com.leaseapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByMobileNumber(String mobileNumber);

    List<Order> findByIsClosed(boolean isClosed);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findWithLockByOrderNumber(UUID orderNumber);
}
