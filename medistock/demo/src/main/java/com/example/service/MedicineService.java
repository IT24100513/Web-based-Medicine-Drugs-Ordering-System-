package com.example.service;

import com.example.model.Medicine;
import com.example.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository repository;

    public MedicineService(MedicineRepository repository) {
        this.repository = repository;
    }

    public List<Medicine> getAll() {
        return repository.findAll();
    }

    public Medicine save(Medicine medicine) {
        return repository.save(medicine);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Optional<Medicine> updateQuantity(Long id, int quantity) {
        return repository.findById(id).map(med -> {
            med.setQuantity(quantity);
            return repository.save(med);
        });
    }
}
