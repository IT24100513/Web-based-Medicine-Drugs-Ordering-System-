package com.sliit.pharmacy_system.config;

import com.sliit.pharmacy_system.entity.AdminUser;
import com.sliit.pharmacy_system.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (adminUserRepository.findByUsername("admin").isEmpty()) {
            // Create default admin user
            AdminUser adminUser = new AdminUser();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(AdminUser.AdminRole.MANAGER);
            
            adminUserRepository.save(adminUser);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }
    }
}
