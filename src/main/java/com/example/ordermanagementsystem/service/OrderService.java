package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    List<Order> getOrdersByEmail(String email);
    Order saveOrder(Order order);
    Order getOrderById(Long id);
    Order updateOrder(Order order);
    void deleteOrderById(Long id);
    boolean isOrderOwnedByEmail(Long orderId, String email);
    boolean canCustomerModifyOrder(Long orderId);
}