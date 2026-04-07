package com.skill_swap_network.skill.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.skill_swap_network.skill.dtos.MatchResponse;
import com.skill_swap_network.skill.dtos.SkillAddRequest;
import com.skill_swap_network.skill.dtos.SkillAddResponse;
import com.skill_swap_network.skill.service.SkillService;
import com.skill_swap_network.user.model.User;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/add")
    public ResponseEntity<SkillAddResponse> addSkill(
            @RequestBody SkillAddRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        SkillAddResponse response = skillService.addSkill(request, user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-skills")
    public ResponseEntity<List<SkillAddResponse>> getMySkills(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<SkillAddResponse> response = skillService.getMySkills(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillAddResponse> getSkillById(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        SkillAddResponse response = skillService.getSkillById(id, user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillAddResponse> updateSkill(
            @PathVariable Long id,
            @RequestBody SkillAddRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        SkillAddResponse response = skillService.updateSkill(id, request, user);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        String response = skillService.deleteSkill(id, user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/matches")
    public ResponseEntity<List<SkillAddResponse>> getMatches(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<SkillAddResponse> matches = skillService.findMatches(user);

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/matches/ranked")
    public ResponseEntity<List<MatchResponse>> getRankedMatches(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<MatchResponse> matches = skillService.findRankedMatches(user);

        return ResponseEntity.ok(matches);
    }
}