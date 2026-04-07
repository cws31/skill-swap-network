package com.skill_swap_network.user.dto;

import com.skill_swap_network.skill.dtos.SkillAddResponse;
import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String bio;
    private String profileImage;
    private ROLE role;
    private STATUS active;
    private List<SkillAddResponse> skills;
}