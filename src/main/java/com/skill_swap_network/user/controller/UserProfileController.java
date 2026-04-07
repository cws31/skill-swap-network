package com.skill_swap_network.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skill_swap_network.user.dto.UpdateProfileRequest;
import com.skill_swap_network.user.dto.UserProfileResponse;
import com.skill_swap_network.user.model.User;
import com.skill_swap_network.user.service.UserProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService profileService;

    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @PutMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @ModelAttribute UpdateProfileRequest request, // ✅ validation works here
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                profileService.updateProfile(
                        user,
                        request.getName(),
                        request.getBio(),
                        image));
    }
}