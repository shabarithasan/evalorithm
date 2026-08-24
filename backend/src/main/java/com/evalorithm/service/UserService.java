package com.evalorithm.service;

import com.evalorithm.dto.request.ChangePasswordRequest;
import com.evalorithm.dto.request.ProfileUpdateRequest;
import com.evalorithm.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateUser(Long id, ProfileUpdateRequest request);

    void changePassword(Long id, ChangePasswordRequest request);
}
