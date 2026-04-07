package com.skill_swap_network.skill.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchSkillDTO {

    private String skillName;
    private String category;
    private String matchType;
}