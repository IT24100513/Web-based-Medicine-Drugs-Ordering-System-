package com.sliit.pharmacy_system.repository;

import com.sliit.pharmacy_system.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    /**
     * Find admin user by username
     * @param username the username to search for
     * @return Optional containing the AdminUser if found
     */
    Optional<AdminUser> findByUsername(String username);
    
    /**
     * Check if admin user exists by username
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Find admin users by role
     * @param role the AdminRole to search for
     * @return List of AdminUsers with the specified role
     */
    java.util.List<AdminUser> findByRole(AdminUser.AdminRole role);
    
    /**
     * Find admin user by username and password (for authentication)
     * @param username the username
     * @param password the password
     * @return Optional containing the AdminUser if credentials match
     */
    Optional<AdminUser> findByUsernameAndPassword(String username, String password);
}
