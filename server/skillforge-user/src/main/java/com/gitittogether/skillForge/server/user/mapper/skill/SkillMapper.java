package com.gitittogether.skillForge.server.user.mapper.skill;

import com.gitittogether.skillForge.server.user.dto.request.skill.SkillRequest;
import com.gitittogether.skillForge.server.user.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.user.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.user.model.skill.Skill;

public class SkillMapper {

    public static SkillResponse toSkillResponse(Skill model) {
        if (model == null) return null;
        return SkillResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .difficultyLevel(model.getLevel())
                .category(CategoryMapper.toCategoryResponse(model.getCategory()))
                .build();
    }

    public static SkillRequest toSkillRequest(Skill model) {
        if (model == null) return null;
        return SkillRequest.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .level(model.getLevel())
                .category(CategoryMapper.toCategoryRequest(model.getCategory()))
                .build();
    }

    public static Skill requestToSkill(SkillRequest request) {
        if (request == null) return null;
        return Skill.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel())
                .category(CategoryMapper.requestToCategory(request.getCategory()))
                .build();
    }

    public static Skill responseToSkill(SkillResponse response) {
        if (response == null) return null;
        return Skill.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .level(response.getDifficultyLevel())
                .category(CategoryMapper.responseToCategory(response.getCategory()))
                .build();
    }
}
