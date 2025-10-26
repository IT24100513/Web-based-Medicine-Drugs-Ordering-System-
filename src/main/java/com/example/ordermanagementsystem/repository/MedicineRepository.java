package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findAllByOrderByNameAsc();
    Medicine findByName(String name);
}