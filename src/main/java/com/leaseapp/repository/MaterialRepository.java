package com.leaseapp.repository;

import com.leaseapp.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    boolean existsByMaterialNameIgnoreCase(String materialName);

    // Pessimistic lock so concurrent lend/return requests can't oversell stock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Material> findWithLockById(UUID id);
}
