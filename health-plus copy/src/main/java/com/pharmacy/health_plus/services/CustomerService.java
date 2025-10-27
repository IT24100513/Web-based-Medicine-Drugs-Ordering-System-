package com.pharmacy.health_plus.services;

import com.pharmacy.health_plus.models.AppUser;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {

    AppUser getAppUserById(Integer id);

    AppUser getAppUserByEmail(String email);

    AppUser updateAppUser(AppUser appUser);

    void deleteAppUserById(Integer id);

    void deleteAppUserByEmail(String email);


}
