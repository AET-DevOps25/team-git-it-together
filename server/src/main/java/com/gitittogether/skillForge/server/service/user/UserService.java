package com.gitittogether.skillForge.server.service.user;

import com.gitittogether.skillForge.server.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;

public interface UserService {
    UserRegisterResponse registerUser(UserRegisterRequest request);

    UserLoginResponse authenticateUser(UserLoginRequest request);

    UserProfileResponse getUserProfile(String userId);

    UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request);
}
