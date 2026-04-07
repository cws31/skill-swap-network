package com.skill_swap_network.Auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skill_swap_network.auth.dto.*;
import com.skill_swap_network.auth.service.Authservice;
import com.skill_swap_network.auth.service.Jwtservice;
import com.skill_swap_network.common.exception.InvalidCredentialException;
import com.skill_swap_network.common.exception.UserAlReadyExistExceeption;
import com.skill_swap_network.common.exception.userNotFoundException;
import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;
import com.skill_swap_network.user.model.User;
import com.skill_swap_network.user.repository.UserRepository;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Jwtservice jwtservice;

    @InjectMocks
    private Authservice authservice;

    @Test
    void registerUser_success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(ROLE.USER);
        savedUser.setActive(STATUS.YES);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserRegistrationResponse response = authservice.registerUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(ROLE.USER, response.getRole());
        assertEquals(STATUS.YES, response.getActive());
    }

    @Test
    void registerUser_userAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserAlReadyExistExceeption.class, () -> authservice.registerUser(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(ROLE.USER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtservice.generateToken(user.getEmail(), user.getRole())).thenReturn("token123");

        LoginResponse response = authservice.login(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertEquals("token123", response.getToken());
    }

    @Test
    void login_invalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialException.class, () -> authservice.login(request));
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(userNotFoundException.class, () -> authservice.login(request));
    }
}