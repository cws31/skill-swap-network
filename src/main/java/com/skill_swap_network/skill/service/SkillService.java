package com.skill_swap_network.skill.service;

import java.util.stream.Collectors;
import java.util.*;
import org.springframework.stereotype.Service;

import com.skill_swap_network.common.exception.ResourceNotFoundException;
import com.skill_swap_network.common.exception.SkillAlreadyExistException;
import com.skill_swap_network.common.exception.UnauthorizedException;
import com.skill_swap_network.skill.dtos.MatchResponse;
import com.skill_swap_network.skill.dtos.MatchSkillDTO;
import com.skill_swap_network.skill.dtos.SkillAddRequest;
import com.skill_swap_network.skill.dtos.SkillAddResponse;
import com.skill_swap_network.skill.enums.SKILLTYPE;
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

    public List<SkillAddResponse> findMatches(User user) {

        List<Skill> mySkills = skillRepository.findByUser(user);

        List<String> teachSkills = mySkills.stream()
                .filter(s -> s.getSkillType().name().equals("TEACH"))
                .map(Skill::getSkillName)
                .toList();

        List<String> learnSkills = mySkills.stream()
                .filter(s -> s.getSkillType().name().equals("LEARN"))
                .map(Skill::getSkillName)
                .toList();

        List<Skill> matchedSkills = new ArrayList<>();

        if (!learnSkills.isEmpty()) {
            matchedSkills.addAll(
                    skillRepository.findMatchingSkills(
                            learnSkills,
                            SKILLTYPE.TEACH,
                            user));
        }

        if (!teachSkills.isEmpty()) {
            matchedSkills.addAll(
                    skillRepository.findMatchingSkills(
                            teachSkills,
                            SKILLTYPE.LEARN,
                            user));
        }

        return matchedSkills.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MatchResponse> findRankedMatches(User user) {

        List<Skill> mySkills = skillRepository.findByUser(user);

        List<String> teachSkills = mySkills.stream()
                .filter(s -> s.getSkillType() == SKILLTYPE.TEACH)
                .map(Skill::getSkillName)
                .toList();

        List<String> learnSkills = mySkills.stream()
                .filter(s -> s.getSkillType() == SKILLTYPE.LEARN)
                .map(Skill::getSkillName)
                .toList();

        List<Skill> allSkills = skillRepository.findAll();

        Map<User, Integer> scoreMap = new HashMap<>();
        Map<User, List<MatchSkillDTO>> matchDetailsMap = new HashMap<>();

        for (Skill skill : allSkills) {

            User otherUser = skill.getUser();

            if (otherUser.getId().equals(user.getId()))
                continue;

            int score = scoreMap.getOrDefault(otherUser, 0);
            List<MatchSkillDTO> matchedSkills = matchDetailsMap.getOrDefault(otherUser, new ArrayList<>());

            if (learnSkills.contains(skill.getSkillName())
                    && skill.getSkillType() == SKILLTYPE.TEACH) {

                score += 2;

                matchedSkills.add(new MatchSkillDTO(
                        skill.getSkillName(),
                        skill.getCategory().name(),
                        "THEY_TEACH_YOU"));
            }

            if (teachSkills.contains(skill.getSkillName())
                    && skill.getSkillType() == SKILLTYPE.LEARN) {

                score += 1;

                matchedSkills.add(new MatchSkillDTO(
                        skill.getSkillName(),
                        skill.getCategory().name(),
                        "YOU_TEACH_THEM"));
            }

            scoreMap.put(otherUser, score);
            matchDetailsMap.put(otherUser, matchedSkills);
        }

        return scoreMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new MatchResponse(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue(),
                        matchDetailsMap.get(entry.getKey())))
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();
    }
}