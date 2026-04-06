package com.skill_swap_network.skill.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skill_swap_network.common.exception.ResourceNotFoundException;
import com.skill_swap_network.common.exception.SkillAlreadyExistException;
import com.skill_swap_network.common.exception.UnauthorizedException;
import com.skill_swap_network.skill.dtos.SkillAddRequest;
import com.skill_swap_network.skill.dtos.SkillAddResponse;
import com.skill_swap_network.skill.model.Skill;
import com.skill_swap_network.skill.repository.SkillRepository;
import com.skill_swap_network.user.model.User;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public SkillAddResponse addSkill(SkillAddRequest request, User user) {

        String skillName = request.getSkillName().trim().toLowerCase();

        Skill existedSkill = skillRepository
                .findBySkillNameAndUser(skillName, user);

        if (existedSkill != null) {
            throw new SkillAlreadyExistException("You already have this skill");
        }

        Skill skill = new Skill();
        skill.setSkillName(skillName);
        skill.setSkillDescription(request.getSkillDescription());
        skill.setCategory(request.getCategory());
        skill.setSkillType(request.getSkilltype());
        skill.setUser(user);

        Skill savedSkill = skillRepository.save(skill);

        return mapToResponse(savedSkill);
    }

    public List<SkillAddResponse> getMySkills(User user) {

        List<Skill> skills = skillRepository.findByUser(user);

        return skills.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SkillAddResponse getSkillById(Long skillId, User user) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill you are trying to find, not found"));

        if (!skill.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("you are npt allowed");
        }

        return mapToResponse(skill);
    }

    public SkillAddResponse updateSkill(Long skillId, SkillAddRequest request, User user) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill you are trying to update, not found"));

        if (!skill.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("you are not allowed");
        }

        String skillName = request.getSkillName().trim().toLowerCase();

        Skill existingSkill = skillRepository
                .findBySkillNameAndUser(skillName, user);

        if (existingSkill != null && !existingSkill.getId().equals(skillId)) {
            throw new SkillAlreadyExistException("You already have this skill");
        }

        skill.setSkillName(skillName);
        skill.setSkillDescription(request.getSkillDescription());
        skill.setCategory(request.getCategory());
        skill.setSkillType(request.getSkilltype());

        Skill updatedSkill = skillRepository.save(skill);

        return mapToResponse(updatedSkill);
    }

    public String deleteSkill(Long skillId, User user) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill you are trying to delete, not found"));

        if (!skill.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("you are not allowed");
        }

        skillRepository.delete(skill);

        return "Skill deleted successfully";
    }

    private SkillAddResponse mapToResponse(Skill skill) {

        SkillAddResponse response = new SkillAddResponse();

        response.setId(skill.getId());
        response.setSkillName(skill.getSkillName());
        response.setSkillDescription(skill.getSkillDescription());
        response.setCategory(skill.getCategory());
        response.setSkilltype(skill.getSkillType());

        response.setUserId(skill.getUser().getId());
        response.setUserName(skill.getUser().getName());

        response.setMessage("Success");

        return response;
    }
}