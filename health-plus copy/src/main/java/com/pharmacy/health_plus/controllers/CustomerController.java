package com.pharmacy.health_plus.controllers;

import com.pharmacy.health_plus.models.AppUser;
import com.pharmacy.health_plus.models.RegisterDto;
import com.pharmacy.health_plus.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class CustomerController {

    CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public String viewCustomerProfile(@AuthenticationPrincipal UserDetails user,Model model){
        AppUser appUser=customerService.getAppUserByEmail(user.getUsername());
        model.addAttribute("appUser",appUser);
        return "profile";
    }

    @GetMapping("/edit-profile")
    public String editCustomerForm(@AuthenticationPrincipal UserDetails user, Model model){
        AppUser appUser=customerService.getAppUserByEmail(user.getUsername());
        model.addAttribute("appUser",appUser);

        return "edit-profile";
    }

    @PostMapping("/profile/{id}")
    public String updateCustomer(@PathVariable String id, @ModelAttribute("appUser") AppUser appUser, Model model, BindingResult result){

        int intId=Integer.parseInt(id);
        AppUser existingAppUser = customerService.getAppUserById(intId);

        existingAppUser.setId(intId);
        existingAppUser.setPassword(existingAppUser.getPassword());
        existingAppUser.setCreatedAt(existingAppUser.getCreatedAt());
        existingAppUser.setRole(existingAppUser.getRole());
        existingAppUser.setPhone(appUser.getPhone());
        existingAppUser.setAddress(appUser.getAddress());
        existingAppUser.setEmail(existingAppUser.getEmail());
        existingAppUser.setLastName(appUser.getLastName());
        existingAppUser.setFirstName(appUser.getFirstName());


        customerService.updateAppUser(existingAppUser);
        return "redirect:/profile";
    }

//    @GetMapping("/profile/delete")
//    public String deleteCustomer(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {
//        if (user != null) {
//            customerService.deleteAppUserByEmail(user.getUsername());
//        }
//        // hand off to Spring Security logout
//        return "redirect:/logout";
//    }

    @GetMapping("/profile/delete")
    public String deleteCustomer(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if (user != null) {
            customerService.deleteAppUserByEmail(user.getUsername());
            new SecurityContextLogoutHandler().logout(request, response,
                    (Authentication) SecurityContextHolder.getContext().getAuthentication());
        }
        return "redirect:/";
    }

}
