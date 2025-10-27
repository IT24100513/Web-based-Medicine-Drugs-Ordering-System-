package com.pharmacy.health_plus.services.impl;

import com.pharmacy.health_plus.models.AppUser;
import com.pharmacy.health_plus.repositories.AppUserRepository;
import com.pharmacy.health_plus.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerServiceImpl implements CustomerService {

    private AppUserRepository appUserRepository;

    public CustomerServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AppUser getAppUserByEmail(String email){
        return appUserRepository.findByEmail(email);
    }

    @Override
    public AppUser getAppUserById(Integer id) {
        return appUserRepository.findById(id).get();
    }

    @Override
    public AppUser updateAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public void deleteAppUserById(Integer id) {
        appUserRepository.deleteById(id);
    }

    @Override
    public void deleteAppUserByEmail(String email){
        appUserRepository.delete(appUserRepository.findByEmail(email));
    }
}
