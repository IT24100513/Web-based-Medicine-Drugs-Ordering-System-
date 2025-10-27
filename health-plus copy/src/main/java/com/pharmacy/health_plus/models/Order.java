package com.pharmacy.health_plus.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "gender")
    private String gender;

    @Column(name = "medical_allergies")
    private String medicalAllergies;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "duration")
    private String duration;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    private String status; // e.g., PENDING, PROCESSING, COMPLETED, CANCELLED

    @Column(name = "prescription_image")
    private String prescriptionImage; // Store file path or filename

    public Order() {}

    public Order(String customerName, String phoneNumber, String email, String location,
                 String gender, String medicalAllergies, String productName, Integer quantity,
                 Double price, LocalDate orderDate, String duration, String paymentMethod,
                 String status, String prescriptionImage) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.location = location;
        this.gender = gender;
        this.medicalAllergies = medicalAllergies;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.duration = duration;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.prescriptionImage = prescriptionImage;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMedicalAllergies() { return medicalAllergies; }
    public void setMedicalAllergies(String medicalAllergies) { this.medicalAllergies = medicalAllergies; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPrescriptionImage() { return prescriptionImage; }
    public void setPrescriptionImage(String prescriptionImage) { this.prescriptionImage = prescriptionImage; }

    // Calculate total amount
    public Double getTotalAmount() {
        return quantity != null && price != null && quantity > 0 && price > 0 ? quantity * price : 0.0;
    }
}
