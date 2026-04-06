package com.skill_swap_network.user.model;

import com.skill_swap_network.skill.model.Skill;
import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private ROLE role;
    private String bio;
    private String profileImage;
    private STATUS active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Skill> skill;

}
