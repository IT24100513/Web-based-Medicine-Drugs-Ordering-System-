package com.pharmacy.health_plus.repositories;

import com.pharmacy.health_plus.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Integer> {

    AppUser findByEmail(String email);

}
