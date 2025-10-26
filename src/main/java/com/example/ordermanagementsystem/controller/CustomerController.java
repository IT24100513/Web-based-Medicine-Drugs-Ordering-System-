package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.service.MedicineService;
import com.example.ordermanagementsystem.service.OrderService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer")
@Validated
public class CustomerController {

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidationExceptions(ConstraintViolationException ex, RedirectAttributes redirectAttributes) {
        StringBuilder errors = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> 
            errors.append(violation.getMessage()).append(". "));
        redirectAttributes.addFlashAttribute("error", errors.toString());
        return "redirect:/customer/orders/new";
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final OrderService orderService;
    private final MedicineService medicineService;

    // Directory for prescription uploads
    private static final String UPLOAD_DIRECTORY = "uploads/";

    public CustomerController(OrderService orderService, MedicineService medicineService) {
        this.orderService = orderService;
        this.medicineService = medicineService;
    }

    // ✅ ADD THIS: Customer home page
    @GetMapping("")
    public String customerHome() {
        return "customer_home";
    }

    // ✅ ADD THIS: Show customer login form
    @GetMapping("/login")
    public String showCustomerLogin() {
        return "customer_login";
    }

    // ✅ ADD THIS: Process customer login
    @PostMapping("/login")
    public String processCustomerLogin(@RequestParam String email, Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email address is required");
            return "customer_login";
        }

        String cleanEmail = email.trim().toLowerCase();
        List<Order> customerOrders = orderService.getOrdersByEmail(cleanEmail);

        if (customerOrders.isEmpty()) {
            model.addAttribute("error", "No orders found for this email address: " + cleanEmail);
            return "customer_login";
        }

        redirectAttributes.addAttribute("email", cleanEmail);
        return "redirect:/customer/orders";
    }

    //  ADD THIS: Show create order form for customers
    @GetMapping("/orders/new")
    public String customerCreateOrderForm(Model model) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        model.addAttribute("order", order);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "customer_create_order";
    }

    // Save new customer order
    @PostMapping("/orders/new")
    public String saveCustomerOrder(@RequestParam @NotBlank(message = "Customer name is required") String customerName,
                                    @RequestParam @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String phoneNumber,
                                    @RequestParam @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
                                    @RequestParam @NotBlank(message = "Location is required") String location,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) String medicalAllergies,
                                    @RequestParam @NotBlank(message = "Medicine name is required") String medicineName,
                                    @RequestParam @Positive(message = "Quantity must be greater than zero") Integer quantity,
                                    @RequestParam(required = false) String duration,
                                    @RequestParam @NotBlank(message = "Payment method is required") String paymentMethod,
                                    @RequestParam(required = false) MultipartFile prescriptionImage,
                                    RedirectAttributes redirectAttributes) {

        // Validate phone number
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            logger.warn("Invalid phone number format: {}", phoneNumber);
            redirectAttributes.addFlashAttribute("error", "Phone number must be exactly 10 digits");
            return "redirect:/customer/orders/new";
        }

        // Validate quantity
        if (quantity == null || quantity <= 0) {
            logger.warn("Invalid quantity: {}", quantity);
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/customer/orders/new";
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setLocation(location);
        order.setGender(gender);
        order.setMedicalAllergies(medicalAllergies);
        order.setMedicine(medicineService.getMedicineByName(medicineName));
        order.setQuantity(quantity);
        order.setOrderDate(LocalDate.now());
        order.setDuration(duration);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");

        // Handle file upload
        if (prescriptionImage != null && !prescriptionImage.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + prescriptionImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, prescriptionImage.getBytes());
                order.setPrescriptionImage(fileName);
                logger.debug("Prescription image uploaded: {}", fileName);
            } catch (IOException e) {
                logger.error("Failed to upload prescription image", e);
                redirectAttributes.addFlashAttribute("error", "Failed to upload prescription image: " + e.getMessage());
                return "redirect:/customer/orders/new";
            }
        }

        orderService.saveOrder(order);
        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        redirectAttributes.addAttribute("email", email);
        return "redirect:/customer/orders";
    }

    // View customer orders by email
    //read operation
    @GetMapping("/orders")
    public String viewOrders(@RequestParam(required = false) String email, Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.trim().isEmpty()) {
            return "redirect:/customer/login";
        }
        
        // Clean and normalize the email
        String cleanEmail = email.trim().toLowerCase();
        
        // Fetch orders
        List<Order> orders = orderService.getOrdersByEmail(cleanEmail);
        
        if (orders.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No orders found for this email address: " + cleanEmail);
            return "redirect:/customer/login";
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("customerEmail", cleanEmail);
        return "customer_orders";
    }

    // Edit order form
    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable Long id, @RequestParam String email, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.debug("Editing order ID: {} for email: {}", id, email);
            
            Order order = orderService.getOrderById(id);

            if (order == null) {
                logger.debug("Order not found: {}", id);
                redirectAttributes.addFlashAttribute("error", "Order not found");
                redirectAttributes.addAttribute("email", email);
                return "redirect:/customer/orders";
            }

            if (!orderService.isOrderOwnedByEmail(id, email)) {
                logger.debug("Unauthorized access attempt for order: {}", id);
                redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this order");
                redirectAttributes.addAttribute("email", email);
                return "redirect:/customer/orders";
            }

            // Check if order is editable based on status
            if (!orderService.canCustomerModifyOrder(id)) {
                logger.debug("Attempt to edit non-editable order: {} with status: {}", id, order.getStatus());
                redirectAttributes.addFlashAttribute("error", "This order cannot be edited because it is being processed");
                redirectAttributes.addAttribute("email", email);
                return "redirect:/customer/orders";
            }

            logger.debug("Successfully retrieving edit form for order: {}", id);
            logger.debug("Order details: {}", order);
            logger.debug("Adding to model - medicines count: {}", medicineService.getAllMedicines().size());
            
            model.addAttribute("order", order);
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("customerEmail", email);
            
            return "customer_edit_order";
        } catch (Exception e) {
            logger.error("Exception in editOrder", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while trying to edit the order");
            redirectAttributes.addAttribute("email", email);
            return "redirect:/customer/orders";
        }
    }

    // Update customer order - FIXED VERSION
    @PostMapping("/orders/{id}")
    public String updateCustomerOrder(@PathVariable Long id,
                                      @RequestParam @NotBlank(message = "Customer name is required") String customerName,
                                      @RequestParam @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String phoneNumber,
                                      @RequestParam @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
                                      @RequestParam @NotBlank(message = "Location is required") String location,
                                      @RequestParam(required = false) String gender,
                                      @RequestParam(required = false) String medicalAllergies,
                                      @RequestParam @NotBlank(message = "Medicine name is required") String medicineName,
                                      @RequestParam @Positive(message = "Quantity must be greater than zero") Integer quantity,
                                      @RequestParam(required = false) String duration,
                                      @RequestParam @NotBlank(message = "Payment method is required") String paymentMethod,
                                      @RequestParam(required = false) String orderDate,
                                      @RequestParam(required = false) MultipartFile prescriptionImage,
                                      RedirectAttributes redirectAttributes) {

        logger.debug("Starting update process for order: {}", id);

        // Clean and normalize email
        String cleanEmail = email.trim().toLowerCase();
        logger.debug("Normalized email: {}", cleanEmail);

        // Validate phone number
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            redirectAttributes.addFlashAttribute("error", "Phone number must be exactly 10 digits");
            return "redirect:/customer/orders/edit/" + id + "?email=" + cleanEmail;
        }

        // Security check
        if (!orderService.isOrderOwnedByEmail(id, email)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this order");
            return "redirect:/customer/orders?email=" + cleanEmail;
        }

        // Status check
        if (!orderService.canCustomerModifyOrder(id)) {
            redirectAttributes.addFlashAttribute("error",
                    "This order can no longer be edited. It has been processed by the pharmacist.");
            return "redirect:/customer/orders?email=" + cleanEmail;
        }

        // Validate quantity
        if (quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/customer/orders/edit/" + id + "?email=" + cleanEmail;
        }

        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/customer/orders?email=" + cleanEmail;
        }

        // Update fields
        existingOrder.setCustomerName(customerName);
        existingOrder.setPhoneNumber(phoneNumber);
        existingOrder.setLocation(location);
        existingOrder.setGender(gender);
        existingOrder.setMedicalAllergies(medicalAllergies);
        existingOrder.setMedicine(medicineService.getMedicineByName(medicineName));
        existingOrder.setQuantity(quantity);
        existingOrder.setDuration(duration);
        existingOrder.setPaymentMethod(paymentMethod);

        // Handle file upload if new file is provided
        if (prescriptionImage != null && !prescriptionImage.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + prescriptionImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, prescriptionImage.getBytes());
                existingOrder.setPrescriptionImage(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload prescription image");
                return "redirect:/customer/orders/edit/" + id + "?email=" + cleanEmail;
            }
        }

        logger.debug("Attempting to save order with ID: {}", existingOrder.getId());
        logger.debug("Order status: {}", existingOrder.getStatus());

        // Parse and set order date if provided
        if (orderDate != null && !orderDate.isEmpty()) {
            try {
                existingOrder.setOrderDate(LocalDate.parse(orderDate));
            } catch (Exception e) {
                logger.error("Failed to parse order date: {}", orderDate, e);
            }
        }

        // Set the normalized email before saving
        existingOrder.setEmail(cleanEmail);
        Order savedOrder = orderService.updateOrder(existingOrder);

        if (savedOrder != null) {
            logger.debug("Order updated successfully. Redirecting with email: {}", cleanEmail);

            redirectAttributes.addFlashAttribute("success", "Order updated successfully!");
            // FIXED: Use only redirect URL without addAttribute to avoid duplication
            return "redirect:/customer/orders?email=" + cleanEmail;
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update order. The order may have been processed by the pharmacist.");
            // FIXED: Use only redirect URL without addAttribute to avoid duplication
            return "redirect:/customer/orders?email=" + cleanEmail;
        }
    }

    // Delete customer order
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, @RequestParam String email, RedirectAttributes redirectAttributes) {
        if (!orderService.isOrderOwnedByEmail(id, email)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this order");
        } else {
            orderService.deleteOrderById(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully");
        }
        redirectAttributes.addAttribute("email", email);
        return "redirect:/customer/orders";
    }
}