package com.pharmacy.health_plus.repositories;

import com.pharmacy.health_plus.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
