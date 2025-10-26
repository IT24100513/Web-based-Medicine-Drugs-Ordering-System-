package com.example.medistock.controller;

import com.example.medistock.model.Medicine;
import com.example.medistock.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicines")
public class MedicineWebController {

    @Autowired
    private MedicineRepository repo;

    // 1) List all medicines
    @GetMapping
    public String listMedicines(Model model) {
        model.addAttribute("medicines", repo.findAll());
        return "medicine-list";   // resolves to templates/medicine-list.html
    }

    // 2) Show add form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "medicine-form";
    }

    // 3) Save new medicine (POST /medicines)
    @PostMapping
    public String saveMedicine(@ModelAttribute Medicine medicine) {
        repo.save(medicine);
        return "redirect:/medicines";
    }

    // 4) Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Medicine med = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid medicine Id: " + id));
        model.addAttribute("medicine", med);
        return "medicine-form";
    }

    // 5) Update existing medicine (POST /medicines/{id})
    @PostMapping("/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute Medicine updated) {
        Medicine med = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid medicine Id: " + id));

        med.setName(updated.getName());
        med.setQuantity(updated.getQuantity());
        med.setUnitPrice(updated.getUnitPrice());
        med.setCategory(updated.getCategory());
        med.setExpiry(updated.getExpiry());
        med.setBatch(updated.getBatch());

        repo.save(med);
        return "redirect:/medicines";
    }

    // 6) Delete medicine (GET link)
    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/medicines";
    }
}
