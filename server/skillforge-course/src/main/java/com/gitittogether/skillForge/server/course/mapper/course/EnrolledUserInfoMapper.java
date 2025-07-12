package com.gitittogether.skillForge.server.course.mapper.course;

import com.gitittogether.skillForge.server.course.dto.request.course.EnrolledUserInfoRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.model.course.EnrolledUserInfo;

public class EnrolledUserInfoMapper {
    public static EnrolledUserInfoResponse toEnrolledUserInfoResponse(EnrolledUserInfo model) {
        if (model == null) return null;
        return EnrolledUserInfoResponse.builder()
                .userId(model.getUserId())
                .progress(model.getProgress())
                .skills(model.getSkills())
                .currentLesson(model.getCurrentLesson())
                .totalNumberOfLessons(model.getTotalNumberOfLessons())
                .build();
    }

    public static EnrolledUserInfo toEnrolledUserInfoRequest(EnrolledUserInfoResponse response) {
        if (response == null) return null;
        return EnrolledUserInfo.builder()
                .userId(response.getUserId())
                .progress(response.getProgress())
                .skills(response.getSkills())
                .currentLesson(response.getCurrentLesson())
                .totalNumberOfLessons(response.getTotalNumberOfLessons())
                .build();
    }

    public static EnrolledUserInfo requestToEnrolledUserInfo(EnrolledUserInfoRequest request) {
        if (request == null) return null;
        return EnrolledUserInfo.builder()
                .userId(request.getUserId())
                .progress(request.getProgress())
                .skills(request.getSkills())
                .currentLesson(request.getCurrentLesson())
                .totalNumberOfLessons(request.getTotalNumberOfLessons())
                .build();
    }

    public static EnrolledUserInfo responseToEnrolledUserInfo(EnrolledUserInfoResponse response) {
        if (response == null) return null;
        return EnrolledUserInfo.builder()
                .userId(response.getUserId())
                .progress(response.getProgress())
                .skills(response.getSkills())
                .currentLesson(response.getCurrentLesson())
                .totalNumberOfLessons(response.getTotalNumberOfLessons())
                .build();
    }


}
