package com.pharmacy.health_plus.controllers;

import com.pharmacy.health_plus.models.Order;
import com.pharmacy.health_plus.services.OrderService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
public class OrderController {

    private final OrderService orderService;

    // Directory to store uploaded files
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Home page redirect
    @GetMapping("/orders")
    public String home() {
        return "redirect:/orders";
    }

    // List all orders
    @GetMapping("/orders/list")
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
        return "create_order";
    }

    // Save new order - Simplified version without file binding issues
    @PostMapping("/orders")
    public String saveOrder(@RequestParam String customerName,
                            @RequestParam String phoneNumber,
                            @RequestParam String email,
                            @RequestParam String location,
                            @RequestParam(required = false) String gender,
                            @RequestParam(required = false) String medicalAllergies,
                            @RequestParam String productName,
                            @RequestParam Integer quantity,
                            @RequestParam Double price,
                            @RequestParam String orderDate,
                            @RequestParam(required = false) String duration,
                            @RequestParam String paymentMethod,
                            @RequestParam(required = false) MultipartFile prescriptionImage,
                            RedirectAttributes redirectAttributes) {

        // Validate quantity and price
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/orders/new";
        }
        if (price <= 0) {
            redirectAttributes.addFlashAttribute("error", "Price must be greater than zero");
            return "redirect:/orders/new";
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setLocation(location);
        order.setGender(gender);
        order.setMedicalAllergies(medicalAllergies);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setPrice(price);
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
        return "redirect:/orders";
    }

    // Show edit order form
    @GetMapping("/orders/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "edit_order";
    }

    // Update order - Simplified version without file binding issues
    @PostMapping("/orders/{id}")
    public String updateOrder(@PathVariable Long id,
                              @RequestParam String customerName,
                              @RequestParam String phoneNumber,
                              @RequestParam String email,
                              @RequestParam String location,
                              @RequestParam(required = false) String gender,
                              @RequestParam(required = false) String medicalAllergies,
                              @RequestParam String productName,
                              @RequestParam Integer quantity,
                              @RequestParam Double price,
                              @RequestParam String orderDate,
                              @RequestParam(required = false) String duration,
                              @RequestParam String paymentMethod,
                              @RequestParam String status,
                              @RequestParam(required = false) MultipartFile prescriptionImage,
                              RedirectAttributes redirectAttributes) {

        // Validate quantity and price
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than zero");
            return "redirect:/orders/edit/" + id;
        }
        if (price <= 0) {
            redirectAttributes.addFlashAttribute("error", "Price must be greater than zero");
            return "redirect:/orders/edit/" + id;
        }

        Order existingOrder = orderService.getOrderById(id);

        // Update fields
        existingOrder.setCustomerName(customerName);
        existingOrder.setPhoneNumber(phoneNumber);
        existingOrder.setEmail(email);
        existingOrder.setLocation(location);
        existingOrder.setGender(gender);
        existingOrder.setMedicalAllergies(medicalAllergies);
        existingOrder.setProductName(productName);
        existingOrder.setQuantity(quantity);
        existingOrder.setPrice(price);
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
        return "redirect:/orders";
    }

    // Delete order
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/orders";
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
                        .contentType(MediaType.IMAGE_JPEG) // Adjust based on your image types
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
