package com.sliit.pharmacy_system.service;

import com.sliit.pharmacy_system.entity.User;
import com.sliit.pharmacy_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users
     * @return List of all User entities
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all users by role
     * @param role The UserRole to filter by
     * @return List of User entities with the specified role
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get all active users
     * @return List of active User entities
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Get all active users by role
     * @param role The UserRole to filter by
     * @return List of active User entities with the specified role
     */
    public List<User> getActiveUsersByRole(User.UserRole role) {
        return userRepository.findByRoleAndActive(role, true);
    }

    /**
     * Save user with encoded password
     * @param user the User entity to save
     */
    public void saveUser(User user) {
        // Encode the password using PasswordEncoder
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // Save the user with encoded password
        userRepository.save(user);
    }

    /**
     * Get user by ID
     * @param id the ID of the User to retrieve
     * @return the User entity
     * @throws RuntimeException if user not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Get user by username
     * @param username the username to search for
     * @return Optional containing the User if found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     * @param email the email to search for
     * @return Optional containing the User if found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user with encoded password
     * @param user the User entity to update
     */
    public void updateUser(User user) {
        // Get the existing user to preserve the original password if new password is empty
        User existingUser = getUserById(user.getId());

        // Debug logging
        System.out.println("Existing username: " + existingUser.getUsername());
        System.out.println("New username: " + user.getUsername());
        System.out.println("Existing email: " + existingUser.getEmail());
        System.out.println("New email: " + user.getEmail());

        // Check if username is being changed and if the new username already exists
        if (user.getUsername() != null && !existingUser.getUsername().equals(user.getUsername())) {
            Optional<User> existingUserWithUsername = userRepository.findByUsername(user.getUsername());
            if (existingUserWithUsername.isPresent() && !existingUserWithUsername.get().getId().equals(user.getId())) {
                throw new RuntimeException("Username already exists: " + user.getUsername());
            }
        } else {
            // Keep the existing username if not changed
            user.setUsername(existingUser.getUsername());
        }

        // Check if email is being changed and if the new email already exists
        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            Optional<User> existingUserWithEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                throw new RuntimeException("Email already exists: " + user.getEmail());
            }
        } else {
            // Keep the existing email if not changed
            user.setEmail(existingUser.getEmail());
        }

        // If password is empty or null, keep the existing password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            // Encode the new password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Preserve creation date
        user.setCreatedAt(existingUser.getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Delete user by ID
     * @param id the ID of the User to delete
     */
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Toggle user active status
     * @param id the ID of the User to toggle
     */
    public void toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Search users by name
     * @param searchTerm the term to search for
     * @return List of User entities matching the search term
     */
    public List<User> searchUsersByName(String searchTerm) {
        return userRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Get user count by role
     * @param role the UserRole to count
     * @return the number of users with the specified role
     */
    public long getUserCountByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Get active user count by role
     * @param role the UserRole to count
     * @return the number of active users with the specified role
     */
    public long getActiveUserCountByRole(User.UserRole role) {
        return userRepository.countByRoleAndActive(role, true);
    }

    /**
     * Get all user roles
     * @return List of all UserRole enum values
     */
    public List<User.UserRole> getAllUserRoles() {
        return List.of(User.UserRole.values());
    }
    
    /**
     * Check if username exists excluding a specific user (for edit mode)
     * @param username the username to check
     * @param excludeId the user ID to exclude from check
     * @return true if username exists (excluding the specified user), false otherwise
     */
    public boolean usernameExistsExcludingUser(String username, Long excludeId) {
        if (excludeId == null) {
            return usernameExists(username);
        }
        return userRepository.findByUsername(username)
                .map(user -> !user.getId().equals(excludeId))
                .orElse(false);
    }
    
    /**
     * Check if email exists excluding a specific user (for edit mode)
     * @param email the email to check
     * @param excludeId the user ID to exclude from check
     * @return true if email exists (excluding the specified user), false otherwise
     */
    public boolean emailExistsExcludingUser(String email, Long excludeId) {
        if (excludeId == null) {
            return emailExists(email);
        }
        return userRepository.findByEmail(email)
                .map(user -> !user.getId().equals(excludeId))
                .orElse(false);
    }
}