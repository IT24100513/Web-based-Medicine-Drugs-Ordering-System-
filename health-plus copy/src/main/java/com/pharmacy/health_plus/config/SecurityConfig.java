package com.pharmacy.health_plus.config;


import com.pharmacy.health_plus.models.AppUser;
import com.pharmacy.health_plus.repositories.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.PasswordManagementDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/profile").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form->form
                        .loginPage("/login")
                        .defaultSuccessUrl("/",true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        //.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // enable GET /logout
                        .logoutSuccessUrl("/")                                            // guest homepage
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserRepository repo) {
        return (String usernameFieldValue) -> {        // this will be the email typed into "Username"
            AppUser user = repo.findByEmail(usernameFieldValue);
            if (user == null) throw new UsernameNotFoundException("No user with email " + usernameFieldValue);

            String normalizedRole = (user.getRole() == null || user.getRole().isBlank())
                    ? "USER"
                    : user.getRole().toUpperCase().replace(' ', '_'); // "Registered Customer" -> "REGISTERED_CUSTOMER"

            return User.withUsername(user.getEmail())
                    .password(user.getPassword()) // bcrypt hash from DB
                    .authorities(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
                    .build();
        };
    }
}
