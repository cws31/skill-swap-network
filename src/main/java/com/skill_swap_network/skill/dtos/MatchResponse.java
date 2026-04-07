package com.skill_swap_network.skill.dtos;

import java.util.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {

    private Long userId;
    private String userName;
    private int score;

    private List<MatchSkillDTO> matchedSkills;
}