package com.skill_swap_network.skill.model;

import com.skill_swap_network.skill.enums.CATEGORY;
import com.skill_swap_network.skill.enums.SKILLTYPE;
import com.skill_swap_network.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;
    private String skillDescription;
    private CATEGORY category;
    private SKILLTYPE skillType;

    @ManyToOne
    private User user;

}
