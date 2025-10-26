package com.sliit.pharmacy_system.service;

import com.sliit.pharmacy_system.entity.AdminUser;
import com.sliit.pharmacy_system.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Get all admin users
     * @return List of all AdminUser entities
     */
    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }
    
    /**
     * Save admin user with encoded password
     * @param adminUser the AdminUser entity to save
     */
    public void saveAdminUser(AdminUser adminUser) {
        // Get the user's password
        String rawPassword = adminUser.getPassword();
        
        // Encode the password using PasswordEncoder
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Set the encoded password back onto the adminUser object
        adminUser.setPassword(encodedPassword);
        
        // Save the admin user with encoded password
        adminUserRepository.save(adminUser);
    }
    
    /**
     * Get admin user by ID
     * @param id the ID of the AdminUser to retrieve
     * @return the AdminUser entity
     * @throws RuntimeException if admin user not found
     */
    public AdminUser getAdminUserById(Long id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));
    }
    
    /**
     * Update admin user with encoded password
     * @param adminUser the AdminUser entity to update
     */
    public void updateAdminUser(AdminUser adminUser) {
        // Get the existing user to preserve the original password if new password is empty
        AdminUser existingUser = getAdminUserById(adminUser.getId());
        
        // If password is empty or null, keep the existing password
        if (adminUser.getPassword() == null || adminUser.getPassword().trim().isEmpty()) {
            adminUser.setPassword(existingUser.getPassword());
        } else {
            // Encode the new password
            adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        }
        
        adminUserRepository.save(adminUser);
    }
    
    /**
     * Delete admin user by ID
     * @param id the ID of the AdminUser to delete
     */
    public void deleteAdminUserById(Long id) {
        adminUserRepository.deleteById(id);
    }
}
