package com.skill_swap_network.Auth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skill_swap_network.auth.service.Jwtservice;
import com.skill_swap_network.user.enums.ROLE;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

class JwtServiceTest {

    private Jwtservice jwtservice;

    @BeforeEach
    void setUp() {
        jwtservice = new Jwtservice();
    }

    @Test
    void generateToken_and_validate() {
        String email = "test@example.com";
        ROLE role = ROLE.USER;

        String token = jwtservice.generateToken(email, role);

        assertNotNull(token);
        assertEquals(email, jwtservice.extractEmail(token));
        assertEquals("USER", jwtservice.extractRole(token));
        assertTrue(jwtservice.isTokenValid(token, email));
    }

}