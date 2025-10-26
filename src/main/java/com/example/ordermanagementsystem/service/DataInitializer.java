package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.Medicine;
import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.repository.MedicineRepository;
import com.example.ordermanagementsystem.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineService medicineService;

    @PostConstruct
    public void init() {
        // Initialize medicines first
        medicineService.initializeMedicines();

        if (orderRepository.count() == 0) {
            Medicine paracetamol = medicineRepository.findByName("Paracetamol");
            Medicine amoxicillin = medicineRepository.findByName("Amoxicillin");
            Medicine vitaminC = medicineRepository.findByName("Vitamin C");

            Order order1 = new Order("John Doe", "1234567890", "john@example.com", "123 Main St",
                    "Male", "None", paracetamol, 50, "2 weeks", "Credit Card", null);

            Order order2 = new Order("Jane Smith", "0987654321", "jane@example.com", "456 Oak Ave",
                    "Female", "Penicillin", amoxicillin, 30, "1 week", "PayPal", null);

            Order order3 = new Order("Bob Wilson", "5551234567", "bob@example.com", "789 Pine Rd",
                    "Male", "Sulfa", vitaminC, 100, "1 month", "Cash", null);

            orderRepository.saveAll(List.of(order1, order2, order3));
            logger.info("Initial orders data loaded successfully with {} orders", 3);
        } else {
            logger.info("Orders data already exists. Skipping initialization");
        }
    }
}