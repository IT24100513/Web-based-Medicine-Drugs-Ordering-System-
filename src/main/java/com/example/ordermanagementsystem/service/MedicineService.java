package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.Medicine;
import java.util.List;

public interface MedicineService {
    List<Medicine> getAllMedicines();
    Medicine getMedicineByName(String name);
    void initializeMedicines();
}