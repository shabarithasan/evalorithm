import api from './api';
import { Setting, SettingRequest, ApiResponse } from '../types';

const settingService = {
  getAll: async (): Promise<ApiResponse<Setting[]>> => {
    const response = await api.get<ApiResponse<Setting[]>>('/settings');
    return response.data;
  },

  getByKey: async (key: string): Promise<ApiResponse<Setting>> => {
    const response = await api.get<ApiResponse<Setting>>(`/settings/${key}`);
    return response.data;
  },

  update: async (data: SettingRequest): Promise<ApiResponse<Setting>> => {
    const response = await api.put<ApiResponse<Setting>>('/settings', data);
    return response.data;
  },
};

export default settingService;
