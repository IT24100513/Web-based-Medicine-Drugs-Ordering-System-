package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.service.OrderService;
import com.example.ordermanagementsystem.service.MedicineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final MedicineService medicineService;

    // Directory to store uploaded files
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    public OrderController(OrderService orderService, MedicineService medicineService) {
        this.orderService = orderService;
        this.medicineService = medicineService;
    }

    // Home page redirect
    @GetMapping("/")
    public String home() {
        return "redirect:/orders";
    }

    // List all orders
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }

    // Show create order form
    @GetMapping("/orders/new")
    public String createOrderForm(Model model) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        model.addAttribute("order", order);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "create_order";
    }

    // Save new order
    @PostMapping("/orders")
    public String saveOrder(@RequestParam String customerName,
                            @RequestParam String phoneNumber,
                            @RequestParam String email,
                            @RequestParam String location,
                            @RequestParam(required = false) String gender,
                            @RequestParam(required = false) String medicalAllergies,
                            @RequestParam String medicineName,
                            @RequestParam Integer quantity,
                            @RequestParam String orderDate,
                            @RequestParam(required = false) String duration,
                            @RequestParam String paymentMethod,
                            @RequestParam(required = false) MultipartFile prescriptionImage,
                            RedirectAttributes redirectAttributes) {

        // Validate quantity
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/orders/new";
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
        order.setOrderDate(LocalDate.parse(orderDate));
        order.setDuration(duration);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");

        // Handle file upload
        if (prescriptionImage != null && !prescriptionImage.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename to avoid overwriting
                String fileName = System.currentTimeMillis() + "_" + prescriptionImage.getOriginalFilename();

                // Save file
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, prescriptionImage.getBytes());
                order.setPrescriptionImage(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload prescription image");
                return "redirect:/orders/new";
            }
        }

        orderService.saveOrder(order);
        redirectAttributes.addFlashAttribute("success", "Order created successfully!");
        return "redirect:/orders";
    }

    // Show edit order form
    @GetMapping("/orders/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "edit_order";
    }

    // Update order
    @PostMapping("/orders/{id}")
    public String updateOrder(@PathVariable Long id,
                              @RequestParam String customerName,
                              @RequestParam String phoneNumber,
                              @RequestParam String email,
                              @RequestParam String location,
                              @RequestParam(required = false) String gender,
                              @RequestParam(required = false) String medicalAllergies,
                              @RequestParam String medicineName,
                              @RequestParam Integer quantity,
                              @RequestParam String orderDate,
                              @RequestParam(required = false) String duration,
                              @RequestParam String paymentMethod,
                              @RequestParam String status,
                              @RequestParam(required = false) MultipartFile prescriptionImage,
                              RedirectAttributes redirectAttributes) {

        // Validate quantity
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/orders/edit/" + id;
        }

        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/orders";
        }

        // Update fields
        existingOrder.setCustomerName(customerName);
        existingOrder.setPhoneNumber(phoneNumber);
        existingOrder.setEmail(email);
        existingOrder.setLocation(location);
        existingOrder.setGender(gender);
        existingOrder.setMedicalAllergies(medicalAllergies);
        existingOrder.setMedicine(medicineService.getMedicineByName(medicineName));
        existingOrder.setQuantity(quantity);
        existingOrder.setOrderDate(LocalDate.parse(orderDate));
        existingOrder.setDuration(duration);
        existingOrder.setPaymentMethod(paymentMethod);
        existingOrder.setStatus(status);

        // Handle file upload only if a new file is provided
        if (prescriptionImage != null && !prescriptionImage.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename to avoid overwriting
                String fileName = System.currentTimeMillis() + "_" + prescriptionImage.getOriginalFilename();

                // Save file
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, prescriptionImage.getBytes());
                existingOrder.setPrescriptionImage(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload prescription image");
                return "redirect:/orders/edit/" + id;
            }
        }

        orderService.updateOrder(existingOrder);
        redirectAttributes.addFlashAttribute("success", "Order updated successfully!");
        return "redirect:/orders";
    }

    // Delete order
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderById(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete order: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    // Accept order (change status from PENDING to PROCESSING)
    @GetMapping("/orders/accept/{id}")
    public String acceptOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Order order = orderService.getOrderById(id);
        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setStatus("PROCESSING");
            order.setRejectReason(null); // Clear any previous reject reason
            orderService.updateOrder(order);
            redirectAttributes.addFlashAttribute("success", "Order accepted and status changed to PROCESSING!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cannot accept order. It may not be in PENDING status.");
        }
        return "redirect:/orders";
    }

    // Reject order with reason
    @PostMapping("/orders/reject/{id}")
    public String rejectOrder(@PathVariable Long id,
                              @RequestParam String rejectReason,
                              RedirectAttributes redirectAttributes) {
        Order order = orderService.getOrderById(id);
        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setStatus("REJECTED");
            order.setRejectReason(rejectReason);
            orderService.updateOrder(order);
            redirectAttributes.addFlashAttribute("success", "Order rejected successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cannot reject order. It may not be in PENDING status.");
        }
        return "redirect:/orders";
    }

    // Show reject reason modal
    @GetMapping("/orders/reject/{id}")
    public String showRejectForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "reject_order_modal"; // This will be a modal fragment
    }

    // Serve uploaded images
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIRECTORY).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}