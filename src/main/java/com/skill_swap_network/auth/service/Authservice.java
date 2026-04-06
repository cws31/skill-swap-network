package com.skill_swap_network.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skill_swap_network.auth.dto.*;
import com.skill_swap_network.common.exception.*;
import com.skill_swap_network.user.model.User;
import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;
import com.skill_swap_network.user.repository.UserRepository;

@Service
public class Authservice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtservice jwtservice;

    public Authservice(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Jwtservice jwtservice) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtservice = jwtservice;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlReadyExistExceeption("User already exists with this email");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ROLE.USER);
        user.setActive(STATUS.YES);

        User savedUser = userRepository.save(user);

        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getRole(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getActive());
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new userNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        String token = jwtservice.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name());
    }
}