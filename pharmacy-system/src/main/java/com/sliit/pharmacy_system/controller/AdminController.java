package com.sliit.pharmacy_system.controller;

import com.sliit.pharmacy_system.entity.AdminUser;
import com.sliit.pharmacy_system.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/admin-users")
public class AdminController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * Display the admin users page
     * @param model the model to add attributes
     * @return the view name for admin users page
     */
    @GetMapping
    public String viewAdminUsersPage(Model model) {
        model.addAttribute("listAdminUsers", adminUserService.getAllAdminUsers());
        return "admin/admin_users";
    }
    
    /**
     * Show the form to add a new admin user
     * @param model the model to add attributes
     * @return the view name for add admin user form
     */
    @GetMapping("/add")
    public String showNewAdminUserForm(Model model) {
        model.addAttribute("adminUser", new AdminUser());
        model.addAttribute("roles", AdminUser.AdminRole.values());
        return "admin/add_admin_user";
    }
    
    /**
     * Save a new admin user
     * @param adminUser the admin user to save
     * @return redirect to admin users page
     */
    @PostMapping("/save")
    public String saveAdminUser(@ModelAttribute("adminUser") AdminUser adminUser) {
        adminUserService.saveAdminUser(adminUser);
        return "redirect:/admin/admin-users";
    }
    
    /**
     * Show the form to edit an existing admin user
     * @param id the ID of the admin user to edit
     * @param model the model to add attributes
     * @return the view name for edit admin user form
     */
    @GetMapping("/edit/{id}")
    public String showEditAdminUserForm(@PathVariable("id") Long id, Model model) {
        AdminUser adminUser = adminUserService.getAdminUserById(id);
        model.addAttribute("adminUser", adminUser);
        model.addAttribute("roles", Arrays.asList(AdminUser.AdminRole.values()));
        return "admin/edit_admin_user";
    }
    
    /**
     * Update an existing admin user
     * @param adminUser the admin user to update
     * @return redirect to admin users page
     */
    @PostMapping("/update")
    public String updateAdminUser(@ModelAttribute("adminUser") AdminUser adminUser) {
        adminUserService.updateAdminUser(adminUser);
        return "redirect:/admin/admin-users?updated=true";
    }
    
    /**
     * Delete an admin user by ID
     * @param id the ID of the admin user to delete
     * @return redirect to admin users page
     */
    @GetMapping("/delete/{id}")
    public String deleteAdminUser(@PathVariable("id") Long id) {
        adminUserService.deleteAdminUserById(id);
        return "redirect:/admin/admin-users";
    }
}
