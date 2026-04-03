package com.skill_swap_network.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skill_swap_network.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
