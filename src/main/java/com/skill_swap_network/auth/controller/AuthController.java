package com.skill_swap_network.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skill_swap_network.auth.dto.LoginRequest;
import com.skill_swap_network.auth.dto.LoginResponse;
import com.skill_swap_network.auth.dto.UserRegistrationRequest;
import com.skill_swap_network.auth.dto.UserRegistrationResponse;
import com.skill_swap_network.auth.service.Authservice;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Authservice authservice;

    public AuthController(Authservice authservice) {
        this.authservice = authservice;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {

        System.out.println("-----------------------************-------------*********-------------");
        UserRegistrationResponse response = authservice.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authservice.login(request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
