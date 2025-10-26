package com.sliit.pharmacy_system.service;

import com.sliit.pharmacy_system.entity.User;
import com.sliit.pharmacy_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Check if user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        // Create authorities based on the user role
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Add role-based authorities
        switch (user.getRole()) {
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case MANAGER:
                authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
                break;
            case PHARMACIST:
                authorities.add(new SimpleGrantedAuthority("ROLE_PHARMACIST"));
                break;
            case INVENTORY_MANAGER:
                authorities.add(new SimpleGrantedAuthority("ROLE_INVENTORY_MANAGER"));
                break;
            case SUPPLIER:
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPPLIER"));
                break;
            case DELIVERY_STAFF:
                authorities.add(new SimpleGrantedAuthority("ROLE_DELIVERY_STAFF"));
                break;
            case IT_OFFICER:
                authorities.add(new SimpleGrantedAuthority("ROLE_IT_OFFICER"));
                break;
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}
