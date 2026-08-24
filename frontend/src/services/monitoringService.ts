import api from './api';
import { AdminMonitoringData, ApiResponse } from '../types';

const monitoringService = {
  getDashboard: async (): Promise<ApiResponse<AdminMonitoringData>> => {
    const response = await api.get<ApiResponse<AdminMonitoringData>>('/monitoring/dashboard');
    return response.data;
  },
  getSystemHealth: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/monitoring/system-health');
    return response.data;
  },
  getDatabaseHealth: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/monitoring/database-health');
    return response.data;
  },
  getStorageUsage: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/monitoring/storage');
    return response.data;
  },
  getOnlineUsers: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/monitoring/online-users');
    return response.data;
  },
};

export default monitoringService;
