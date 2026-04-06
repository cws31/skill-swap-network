package com.skill_swap_network.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import com.skill_swap_network.skill.model.Skill;
import com.skill_swap_network.user.model.User;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findBySkillName(String name);

    Skill findBySkillNameAndUser(String skillName, User user);

    List<Skill> findByUser(User user);
}
