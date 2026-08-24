import api from './api';
import { SystemSetting, ApiResponse, PageResponse } from '../types';

const systemSettingService = {
  getAll: async (): Promise<ApiResponse<PageResponse<SystemSetting>>> => {
    const response = await api.get<ApiResponse<PageResponse<SystemSetting>>>('/system-settings');
    return response.data;
  },
  getByCategory: async (category: string): Promise<ApiResponse<PageResponse<SystemSetting>>> => {
    const response = await api.get<ApiResponse<PageResponse<SystemSetting>>>(`/system-settings/category/${category}`);
    return response.data;
  },
  update: async (id: number, data: any): Promise<ApiResponse<SystemSetting>> => {
    const response = await api.put<ApiResponse<SystemSetting>>(`/system-settings/${id}`, data);
    return response.data;
  },
  updateByKey: async (key: string, value: string): Promise<ApiResponse<SystemSetting>> => {
    const response = await api.put<ApiResponse<SystemSetting>>(`/system-settings/key/${key}`, { settingValue: value });
    return response.data;
  },
};

export default systemSettingService;
