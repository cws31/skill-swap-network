package com.skill_swap_network.auth.dto;

import com.skill_swap_network.user.enums.ROLE;
import com.skill_swap_network.user.enums.STATUS;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse {

    private Long id;
    private ROLE role;
    private String name;
    private String Email;
    private STATUS active;

}
