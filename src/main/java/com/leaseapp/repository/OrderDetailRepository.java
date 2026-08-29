package com.leaseapp.repository;

import com.leaseapp.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

    List<OrderDetail> findByOrderNumber(UUID orderNumber);

    List<OrderDetail> findByOrderNumberAndReturnTimestampIsNull(UUID orderNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderDetail> findWithLockById(UUID id);
}
