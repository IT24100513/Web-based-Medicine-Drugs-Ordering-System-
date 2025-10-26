package com.example.medistock.controller;

import com.example.medistock.model.Medicine;
import com.example.medistock.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // allow frontend JS
@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineRepository repo;

    @GetMapping
    public List<Medicine> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine med) {
        System.out.println("Received medicine: " + med.getName() + ", " + med.getQuantity() + ", " + med.getExpiry());
        return repo.save(med);
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(@PathVariable Long id, @RequestBody Medicine updated) {
        Medicine med = repo.findById(id).orElseThrow();
        med.setName(updated.getName());
        med.setQuantity(updated.getQuantity());
        med.setUnitPrice(updated.getUnitPrice());
        med.setCategory(updated.getCategory());
        med.setExpiry(updated.getExpiry());
        med.setBatch(updated.getBatch());
        return repo.save(med);
    }

    @DeleteMapping("/{id}")
    public void deleteMedicine(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
