package com.example.ordermanagementsystem.service.impl;

import com.example.ordermanagementsystem.entity.Medicine;
import com.example.ordermanagementsystem.repository.MedicineRepository;
import com.example.ordermanagementsystem.service.MedicineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private static final Logger logger = LoggerFactory.getLogger(MedicineServiceImpl.class);
    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Medicine getMedicineByName(String name) {
        return medicineRepository.findByName(name);
    }

    @Override
    public void initializeMedicines() {
        if (medicineRepository.count() == 0) {
            List<Medicine> medicines = List.of(
                    new Medicine("Paracetamol", 2.50, "Pain reliever and fever reducer", 100),
                    new Medicine("Amoxicillin", 5.75, "Antibiotic", 50),
                    new Medicine("Vitamin C", 1.25, "Immune system support", 200),
                    new Medicine("Ibuprofen", 3.20, "Anti-inflammatory pain reliever", 150),
                    new Medicine("Aspirin", 2.10, "Pain reliever and blood thinner", 100),
                    new Medicine("Lisinopril", 8.90, "Blood pressure medication", 75),
                    new Medicine("Metformin", 4.30, "Diabetes medication", 80),
                    new Medicine("Atorvastatin", 7.50, "Cholesterol medication", 60),
                    new Medicine("Levothyroxine", 6.25, "Thyroid hormone", 70),
                    new Medicine("Albuterol", 9.10, "Asthma inhaler", 40)
            );

            medicineRepository.saveAll(medicines);
            logger.info("Medicines initialized successfully with {} items", medicines.size());
        }
    }
}