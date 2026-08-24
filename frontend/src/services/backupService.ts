import api from './api';
import { Backup, PageResponse, ApiResponse } from '../types';

const backupService = {
  getAll: async (): Promise<ApiResponse<PageResponse<Backup>>> => {
    const response = await api.get<ApiResponse<PageResponse<Backup>>>('/backups');
    return response.data;
  },
  create: async (data?: any): Promise<ApiResponse<Backup>> => {
    const response = await api.post<ApiResponse<Backup>>('/backups', data || {});
    return response.data;
  },
  restore: async (id: number): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/backups/${id}/restore`);
    return response.data;
  },
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/backups/${id}`);
    return response.data;
  },
  download: (id: number) => api.get(`/backups/${id}/download`, { responseType: 'blob' }),
};

export default backupService;
