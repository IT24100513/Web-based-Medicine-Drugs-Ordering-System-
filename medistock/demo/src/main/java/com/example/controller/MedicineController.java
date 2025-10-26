package com.example.controller;

import com.example.model.Medicine;
import com.example.service.MedicineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")  // Allow requests from your frontend
public class MedicineController {

    private final MedicineService service;

    public MedicineController(MedicineService service) {
        this.service = service;
    }

    @GetMapping
    public List<Medicine> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Medicine create(@RequestBody Medicine medicine) {
        return service.save(medicine);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public Medicine updateQuantity(@PathVariable Long id, @RequestBody Medicine updated) {
        return service.updateQuantity(id, updated.getQuantity())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
    }
}
