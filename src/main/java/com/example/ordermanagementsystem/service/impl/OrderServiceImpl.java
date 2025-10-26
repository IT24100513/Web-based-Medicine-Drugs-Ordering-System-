package com.example.ordermanagementsystem.service.impl;

import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.repository.OrderRepository;
import com.example.ordermanagementsystem.service.OrderService;
import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final Validator validator;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to get orders with null or empty email");
            return List.of();
        }
        
        String cleanEmail = email.trim().toLowerCase();
        logger.debug("Fetching orders for email: {}", cleanEmail);
        
        List<Order> orders = orderRepository.findByEmailOrderByOrderDateDesc(cleanEmail);
        logger.debug("Found {} orders for email: {}", orders.size(), cleanEmail);
        
        return orders;
    }

    @Override
    public Order saveOrder(Order order) {
        if (order == null) {
            logger.error("Attempted to save null order");
            throw new IllegalArgumentException("Order cannot be null");
        }

        // Validate the order
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Order> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            logger.error("Order validation failed: {}", sb);
            throw new ConstraintViolationException("Order validation failed: " + sb, violations);
        }

        // Clean and normalize email
        if (order.getEmail() != null) {
            order.setEmail(order.getEmail().trim().toLowerCase());
        }

        // Set initial status if not set
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("PENDING");
        }

        try {
            Order savedOrder = orderRepository.save(order);
            logger.info("Successfully saved order with ID: {}", savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            logger.error("Failed to save order: {}", e.getMessage());
            throw new RuntimeException("Failed to save order", e);
        }
    }

    @Override
    public Order getOrderById(Long id) {
        if (id == null) {
            logger.warn("Attempted to get order with null ID");
            return null;
        }

        return orderRepository.findById(id)
                .orElseGet(() -> {
                    logger.debug("No order found with ID: {}", id);
                    return null;
                });
    }

    @Override
    public Order updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            logger.error("Attempted to update null order or order without ID");
            throw new IllegalArgumentException("Order and order ID cannot be null");
        }

        // Get existing order
        Order existingOrder = getOrderById(order.getId());
        if (existingOrder == null) {
            logger.error("Attempted to update non-existent order with ID: {}", order.getId());
            return null;
        }

        // Only allow updating PENDING orders
        if (!"PENDING".equalsIgnoreCase(existingOrder.getStatus())) {
            logger.warn("Attempted to update non-PENDING order. ID: {}, Status: {}", 
                    existingOrder.getId(), existingOrder.getStatus());
            return null;
        }

        // Clean and normalize data
        order.setStatus("PENDING"); // Keep as PENDING
        order.setOrderDate(existingOrder.getOrderDate()); // Preserve original date
        order.setEmail(existingOrder.getEmail().trim().toLowerCase()); // Preserve normalized email

        // Validate the updated order
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Order> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            logger.error("Order validation failed during update: {}", sb);
            throw new ConstraintViolationException("Order validation failed: " + sb, violations);
        }

        try {
            Order savedOrder = orderRepository.save(order);
            logger.info("Successfully updated order with ID: {}", savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            logger.error("Failed to update order: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteOrderById(Long id) {
        if (id == null) {
            logger.warn("Attempted to delete order with null ID");
            return;
        }

        try {
            orderRepository.deleteById(id);
            logger.info("Successfully deleted order with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete order with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    @Override
    public boolean isOrderOwnedByEmail(Long orderId, String email) {
        if (orderId == null || email == null) {
            logger.warn("Attempted ownership check with null orderId or email");
            return false;
        }

        String cleanEmail = email.trim().toLowerCase();
        try {
            return orderRepository.existsByIdAndEmail(orderId, cleanEmail);
        } catch (Exception e) {
            logger.error("Error checking order ownership: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canCustomerModifyOrder(Long orderId) {
        if (orderId == null) {
            logger.warn("Attempted modification check with null orderId");
            return false;
        }

        Order order = getOrderById(orderId);
        if (order == null || order.getStatus() == null) {
            logger.warn("Order not found or has null status. ID: {}", orderId);
            return false;
        }

        boolean canModify = "PENDING".equalsIgnoreCase(order.getStatus().trim());
        if (!canModify) {
            logger.debug("Order {} cannot be modified. Current status: {}", 
                    orderId, order.getStatus());
        }
        return canModify;
    }
}
