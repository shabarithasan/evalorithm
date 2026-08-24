import api from './api';
import { AuditLog, LoginHistory, ApiResponse, PageResponse } from '../types';

const auditService = {
  getLogs: async (params?: any): Promise<ApiResponse<PageResponse<AuditLog>>> => {
    const response = await api.get<ApiResponse<PageResponse<AuditLog>>>('/audit/logs', { params });
    return response.data;
  },
  getLoginHistory: async (params?: any): Promise<ApiResponse<PageResponse<LoginHistory>>> => {
    const response = await api.get<ApiResponse<PageResponse<LoginHistory>>>('/audit/login-history', { params });
    return response.data;
  },
  getActiveUsers: async (): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>('/audit/active-users');
    return response.data;
  },
};

export default auditService;
