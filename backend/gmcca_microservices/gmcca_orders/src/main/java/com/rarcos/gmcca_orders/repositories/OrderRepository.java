package com.rarcos.gmcca_orders.repositories;

import com.rarcos.gmcca_orders.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
