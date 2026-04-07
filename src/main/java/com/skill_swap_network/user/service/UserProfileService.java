package com.skill_swap_network.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skill_swap_network.user.dto.UserProfileResponse;
import com.skill_swap_network.user.model.User;
import com.skill_swap_network.user.repository.UserRepository;
import com.skill_swap_network.skill.dtos.SkillAddResponse;
import com.skill_swap_network.skill.model.Skill;
import com.skill_swap_network.skill.repository.SkillRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public UserProfileService(UserRepository userRepository,
            SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public UserProfileResponse getProfile(User user) {
        return mapToResponse(user);
    }

    public UserProfileResponse updateProfile(User user,
            String name,
            String bio,
            MultipartFile image) {

        user.setName(name);
        user.setBio(bio);

        // 🔥 Image Upload
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

                Path path = Paths.get(UPLOAD_DIR + fileName);

                Files.createDirectories(path.getParent());
                Files.write(path, image.getBytes());

                user.setProfileImage("/uploads/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image");
            }
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private UserProfileResponse mapToResponse(User user) {

        UserProfileResponse response = new UserProfileResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setBio(user.getBio());
        response.setProfileImage(user.getProfileImage());
        response.setRole(user.getRole());
        response.setActive(user.getActive());

        // 🔥 FIX: Fetch skills explicitly
        List<Skill> skills = skillRepository.findByUser(user);

        List<SkillAddResponse> skillResponses = skills.stream()
                .map(skill -> {
                    SkillAddResponse s = new SkillAddResponse();
                    s.setId(skill.getId());
                    s.setSkillName(skill.getSkillName());
                    s.setSkillDescription(skill.getSkillDescription());
                    s.setCategory(skill.getCategory());
                    s.setSkilltype(skill.getSkillType());
                    s.setUserId(user.getId());
                    s.setUserName(user.getName());
                    s.setMessage("Success");
                    return s;
                })
                .collect(Collectors.toList());

        response.setSkills(skillResponses);

        return response;
    }
}