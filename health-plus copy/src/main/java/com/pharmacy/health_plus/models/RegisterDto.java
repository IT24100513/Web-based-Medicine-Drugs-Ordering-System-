package com.pharmacy.health_plus.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;

public class RegisterDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(max = 10,message = "eg: 0771234567")
    private String phone;

    @NotEmpty
    private String address;

    @NotEmpty
    @Size(min = 8,message = "Minimum password length is 8 characters")
    private String password;

    @NotEmpty
    private String confirmPassword;

    public RegisterDto() {

    }

    public RegisterDto(String firstName, String lastName, String email, String phone, String address, String password, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
