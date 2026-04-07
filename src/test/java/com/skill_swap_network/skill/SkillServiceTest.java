package com.skill_swap_network.skill;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skill_swap_network.common.exception.*;
import com.skill_swap_network.skill.dtos.*;
import com.skill_swap_network.skill.enums.*;
import com.skill_swap_network.skill.model.Skill;
import com.skill_swap_network.skill.repository.SkillRepository;
import com.skill_swap_network.skill.service.SkillService;
import com.skill_swap_network.user.model.User;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User" + id);
        return user;
    }

    private Skill createSkill(Long id, String name, SKILLTYPE type, User user) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setSkillName(name);
        skill.setSkillType(type);
        skill.setUser(user);
        skill.setCategory(CATEGORY.TECH); // important
        skill.setSkillDescription("desc");
        return skill;
    }

    private SkillAddRequest createRequest(String name, SKILLTYPE type) {
        SkillAddRequest request = new SkillAddRequest();
        request.setSkillName(name);
        request.setSkillDescription("desc");
        request.setCategory(CATEGORY.TECH);
        request.setSkilltype(type);
        return request;
    }

    @Test
    @DisplayName("Add Skill - Success")
    void addSkill_success() {
        User user = createUser(1L);
        SkillAddRequest request = createRequest("Java", SKILLTYPE.TEACH);

        when(skillRepository.findBySkillNameAndUser("java", user)).thenReturn(null);
        when(skillRepository.save(any())).thenReturn(createSkill(1L, "java", SKILLTYPE.TEACH, user));

        SkillAddResponse response = skillService.addSkill(request, user);

        assertNotNull(response);
        assertEquals("java", response.getSkillName());
        assertEquals("Success", response.getMessage());
    }

    @Test
    @DisplayName("Add Skill - Duplicate")
    void addSkill_alreadyExists() {
        User user = createUser(1L);

        when(skillRepository.findBySkillNameAndUser("java", user))
                .thenReturn(new Skill());

        assertThrows(SkillAlreadyExistException.class,
                () -> skillService.addSkill(createRequest("Java", SKILLTYPE.TEACH), user));
    }

    @Test
    void getMySkills_success() {
        User user = createUser(1L);

        when(skillRepository.findByUser(user)).thenReturn(List.of(
                createSkill(1L, "java", SKILLTYPE.TEACH, user),
                createSkill(2L, "python", SKILLTYPE.LEARN, user)));

        List<SkillAddResponse> result = skillService.getMySkills(user);

        assertEquals(2, result.size());
    }

    @Test
    void getMySkills_empty() {
        User user = createUser(1L);

        when(skillRepository.findByUser(user)).thenReturn(Collections.emptyList());

        List<SkillAddResponse> result = skillService.getMySkills(user);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSkillById_success() {
        User user = createUser(1L);

        when(skillRepository.findById(1L))
                .thenReturn(Optional.of(createSkill(1L, "java", SKILLTYPE.TEACH, user)));

        SkillAddResponse response = skillService.getSkillById(1L, user);

        assertEquals("java", response.getSkillName());
    }

    @Test
    void getSkillById_notFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> skillService.getSkillById(1L, createUser(1L)));
    }

    @Test
    void getSkillById_unauthorized() {
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, createUser(1L));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(UnauthorizedException.class,
                () -> skillService.getSkillById(1L, createUser(2L)));
    }

    @Test
    void updateSkill_success() {
        User user = createUser(1L);
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, user);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.findBySkillNameAndUser("python", user)).thenReturn(null);
        when(skillRepository.save(any())).thenReturn(skill);

        SkillAddResponse response = skillService.updateSkill(1L, createRequest("Python", SKILLTYPE.LEARN), user);

        assertEquals("python", response.getSkillName());
    }

    @Test
    void updateSkill_duplicate() {
        User user = createUser(1L);
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, user);
        Skill existing = createSkill(2L, "python", SKILLTYPE.TEACH, user);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.findBySkillNameAndUser("python", user)).thenReturn(existing);

        assertThrows(SkillAlreadyExistException.class,
                () -> skillService.updateSkill(1L, createRequest("Python", SKILLTYPE.TEACH), user));
    }

    @Test
    void updateSkill_unauthorized() {
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, createUser(1L));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(UnauthorizedException.class,
                () -> skillService.updateSkill(1L, createRequest("Python", SKILLTYPE.LEARN), createUser(2L)));
    }

    @Test
    void deleteSkill_success() {
        User user = createUser(1L);
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, user);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        String result = skillService.deleteSkill(1L, user);

        assertEquals("Skill deleted successfully", result);
        verify(skillRepository).delete(skill);
    }

    @Test
    void deleteSkill_notFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> skillService.deleteSkill(1L, createUser(1L)));
    }

    @Test
    void deleteSkill_unauthorized() {
        Skill skill = createSkill(1L, "java", SKILLTYPE.TEACH, createUser(1L));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(UnauthorizedException.class,
                () -> skillService.deleteSkill(1L, createUser(2L)));
    }

    @Test
    void findMatches_success() {
        User user = createUser(1L);

        Skill mySkill = createSkill(1L, "java", SKILLTYPE.LEARN, user);

        when(skillRepository.findByUser(user)).thenReturn(List.of(mySkill));
        when(skillRepository.findMatchingSkills(anyList(), any(), eq(user)))
                .thenReturn(List.of(mySkill));

        List<SkillAddResponse> result = skillService.findMatches(user);

        assertFalse(result.isEmpty());
    }

    @Test
    void findMatches_empty() {
        User user = createUser(1L);

        when(skillRepository.findByUser(user)).thenReturn(Collections.emptyList());

        List<SkillAddResponse> result = skillService.findMatches(user);

        assertTrue(result.isEmpty());
    }

    @Test
    void findRankedMatches_success() {
        User user = createUser(1L);
        User other = createUser(2L);

        Skill mySkill = createSkill(1L, "java", SKILLTYPE.LEARN, user);
        Skill otherSkill = createSkill(2L, "java", SKILLTYPE.TEACH, other);

        when(skillRepository.findByUser(user)).thenReturn(List.of(mySkill));
        when(skillRepository.findAll()).thenReturn(List.of(otherSkill));

        List<MatchResponse> result = skillService.findRankedMatches(user);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());
        assertTrue(result.get(0).getScore() > 0);
    }
}