package com.rarcos.gmcca_inventory.repositories;

import com.rarcos.gmcca_inventory.model.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCode(String code);

    List<Inventory> findByCodeIn(List<String> codes);
}
