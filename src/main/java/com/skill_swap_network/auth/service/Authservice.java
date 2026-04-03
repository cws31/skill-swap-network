package com.skill_swap_network.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skill_swap_network.auth.dto.LoginRequest;
import com.skill_swap_network.auth.dto.LoginResponse;
import com.skill_swap_network.auth.dto.UserRegistrationRequest;
import com.skill_swap_network.auth.dto.UserRegistrationResponse;
import com.skill_swap_network.common.exception.InvalidCredentialException;
import com.skill_swap_network.common.exception.UserAlReadyExistExceeption;
import com.skill_swap_network.common.exception.userNotFoundException;
import com.skill_swap_network.user.repository.UserRepository;
import com.skill_swap_network.user.*;
import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;

@Service
public class Authservice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtservice jwtservice;

    public Authservice(UserRepository userRepository, PasswordEncoder passwordEncoder, Jwtservice jwtservice) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtservice = jwtservice;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new UserAlReadyExistExceeption("user alrady exist with this email try with other email");
        }
        com.skill_swap_network.user.model.User user = new com.skill_swap_network.user.model.User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ROLE.USER);
        user.setActive(STATUS.YES);

        com.skill_swap_network.user.model.User savedUser = userRepository.save(user);

        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getRole(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getActive());

    }

    public LoginResponse login(LoginRequest request) {
        com.skill_swap_network.user.model.User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new userNotFoundException("user not found");
        }

        String password = user.getPassword();
        if (!passwordEncoder.matches(request.getPassword(), password)) {
            throw new InvalidCredentialException("invalid credential");
        }

        String token = jwtservice.generateToken(request.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name());
    }

}
