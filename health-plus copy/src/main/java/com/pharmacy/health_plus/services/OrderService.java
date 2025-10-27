package com.pharmacy.health_plus.services;

import com.pharmacy.health_plus.models.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();
    Order saveOrder(Order order);
    Order getOrderById(Long id);
    Order updateOrder(Order order);
    void deleteOrderById(Long id);
}
