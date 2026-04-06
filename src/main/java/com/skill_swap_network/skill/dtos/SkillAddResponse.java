package com.skill_swap_network.skill.dtos;

import com.skill_swap_network.skill.enums.CATEGORY;
import com.skill_swap_network.skill.enums.SKILLTYPE;
import com.skill_swap_network.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillAddResponse {

    private Long id;
    private String skillName;
    private String skillDescription;
    private CATEGORY category;
    private SKILLTYPE skilltype;

    private Long userId;
    private String userName;

    private String message;
}
