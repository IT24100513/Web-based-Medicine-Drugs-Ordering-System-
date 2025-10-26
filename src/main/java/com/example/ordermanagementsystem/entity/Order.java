package com.example.ordermanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Location is required")
    @Column(name = "location")
    private String location;

    @Column(name = "gender")
    private String gender;

    @Column(name = "medical_allergies")
    private String medicalAllergies;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "duration")
    private String duration;

    @NotBlank(message = "Payment method is required")
    @Column(name = "payment_method")
    private String paymentMethod;

    @NotBlank(message = "Status is required")
    @Column(name = "status")
    private String status;

    @Column(name = "prescription_image")
    private String prescriptionImage;

    @Column(name = "product_name", insertable = false, updatable = false)
    private String productName;

    @Column(name = "reject_reason")
    private String rejectReason;

    @NotNull(message = "Medicine is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    public Order() {
        this.orderDate = LocalDate.now();
        this.status = "PENDING";
    }

    public Order(String customerName, String phoneNumber, String email, String location,
                String gender, String medicalAllergies, Medicine medicine, Integer quantity,
                String duration, String paymentMethod, String prescriptionImage) {
        this();
        this.customerName = customerName != null ? customerName.trim() : null;
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.location = location != null ? location.trim() : null;
        this.gender = gender;
        this.medicalAllergies = medicalAllergies;
        this.medicine = medicine;
        this.quantity = quantity;
        this.duration = duration;
        this.paymentMethod = paymentMethod;
        this.prescriptionImage = prescriptionImage;
        if (medicine != null) {
            this.productName = medicine.getName();
        }
    }

    // Getters and Setters with data cleaning
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { 
        this.customerName = customerName != null ? customerName.trim() : null; 
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null; 
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email != null ? email.trim().toLowerCase() : null; 
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { 
        this.location = location != null ? location.trim() : null; 
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMedicalAllergies() { return medicalAllergies; }
    public void setMedicalAllergies(String medicalAllergies) { 
        this.medicalAllergies = medicalAllergies; 
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { 
        this.orderDate = orderDate != null ? orderDate : LocalDate.now(); 
    }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { 
        this.duration = duration != null ? duration.trim() : null; 
    }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { 
        this.paymentMethod = paymentMethod != null ? paymentMethod.trim() : null; 
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status != null ? status.trim().toUpperCase() : "PENDING"; 
    }

    public String getPrescriptionImage() { return prescriptionImage; }
    public void setPrescriptionImage(String prescriptionImage) { 
        this.prescriptionImage = prescriptionImage; 
    }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { 
        this.rejectReason = rejectReason != null ? rejectReason.trim() : null; 
    }

    public String getProductName() {
        if (productName == null && medicine != null) {
            return medicine.getName();
        }
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName != null ? productName.trim() : null;
    }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (medicine != null) {
            this.productName = medicine.getName();
        }
    }

    // Business logic methods
    public Double getTotalAmount() {
        if (quantity == null || quantity <= 0 || medicine == null || medicine.getPrice() == null) {
            return 0.0;
        }
        return quantity * medicine.getPrice();
    }

    public boolean isEditable() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", quantity=" + quantity +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", medicine=" + (medicine != null ? medicine.getName() : "null") +
                '}';
    }
}