package com.sliit.pharmacy_system.controller;

import com.sliit.pharmacy_system.entity.User;
import com.sliit.pharmacy_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/users")  // Changed from "/users" to "/admin/users"
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Display the users management page
     * @param model the model to add attributes
     * @return the view name for users page
     */
    @GetMapping
    public String viewUsersPage(Model model) {
        model.addAttribute("listUsers", userService.getAllUsers());
        model.addAttribute("roles", Arrays.asList(User.UserRole.values()));
        return "admin/users";
    }
    
    /**
     * Show the form to add a new user
     * @param model the model to add attributes
     * @return the view name for add user form
     */
    @GetMapping("/add")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Arrays.asList(User.UserRole.values()));
        return "admin/add_user";
    }
    
    /**
     * Save a new user
     * @param user the user to save
     * @return redirect to users page
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/users?success=true";
    }
    
    /**
     * Show the form to edit an existing user
     * @param id the ID of the user to edit
     * @param model the model to add attributes
     * @return the view name for edit user form
     */
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(User.UserRole.values()));
        return "admin/edit_user";
    }
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return redirect to users page
     */
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/admin/users?updated=true";
    }
    
    /**
     * Delete a user by ID
     * @param id the ID of the user to delete
     * @return redirect to users page
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users?deleted=true";
    }
    
    /**
     * Toggle user active status
     * @param id the ID of the user to toggle
     * @return redirect to users page
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users?statusChanged=true";
    }
    
    /**
     * Get users by role
     * @param role the role to filter by
     * @param model the model to add attributes
     * @return the view name for users page
     */
    @GetMapping("/role/{role}")
    public String getUsersByRole(@PathVariable("role") String role, Model model) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            model.addAttribute("listUsers", userService.getUsersByRole(userRole));
            model.addAttribute("roles", Arrays.asList(User.UserRole.values()));
            model.addAttribute("selectedRole", userRole);
            return "admin/users";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users?error=invalidRole";
        }
    }
    
    /**
     * Check if username is available
     * @param username the username to check
     * @param excludeId the user ID to exclude from check (for edit mode)
     * @return true if username exists, false otherwise
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsernameAvailability(@RequestParam String username, 
                                           @RequestParam(required = false) Long excludeId) {
        return userService.usernameExistsExcludingUser(username, excludeId);
    }
    
    /**
     * Check if email is available
     * @param email the email to check
     * @param excludeId the user ID to exclude from check (for edit mode)
     * @return true if email exists, false otherwise
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam String email, 
                                        @RequestParam(required = false) Long excludeId) {
        return userService.emailExistsExcludingUser(email, excludeId);
    }
}
