package com.skill_swap_network.skill.dtos;

import com.skill_swap_network.skill.enums.CATEGORY;
import com.skill_swap_network.skill.enums.SKILLTYPE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillAddRequest {

    private String skillName;
    private String skillDescription;
    private CATEGORY category;
    private SKILLTYPE skilltype;

}
