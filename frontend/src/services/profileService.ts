import api from './api';
import { User, ProfileUpdateRequest, ChangePasswordRequest, ApiResponse } from '../types';

const profileService = {
  getCurrentUser: async (): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>('/profile');
    return response.data;
  },

  updateProfile: async (data: ProfileUpdateRequest): Promise<ApiResponse<User>> => {
    const response = await api.put<ApiResponse<User>>('/profile', data);
    return response.data;
  },

  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    await api.put('/profile/change-password', data);
  },
};

export default profileService;
