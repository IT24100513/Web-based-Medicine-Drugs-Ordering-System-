package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE LOWER(o.email) = LOWER(:email) ORDER BY o.orderDate DESC")
    List<Order> findByEmailOrderByOrderDateDesc(@Param("email") String email);

    @Query("SELECT o FROM Order o WHERE o.id = :id AND LOWER(o.email) = LOWER(:email)")
    Optional<Order> findByIdAndEmail(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.id = :id AND LOWER(o.email) = LOWER(:email)")
    boolean existsByIdAndEmail(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC")
    List<Order> findByStatus(@Param("status") String status);
}