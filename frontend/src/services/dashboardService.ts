import api from './api';
import {
  DashboardData,
  FacultyDashboardData,
  StudentDashboardData,
  ApiResponse,
} from '../types';

const dashboardService = {
  getAdminDashboard: async (): Promise<ApiResponse<DashboardData>> => {
    const response = await api.get<ApiResponse<DashboardData>>('/admin/dashboard');
    return response.data;
  },

  getFacultyDashboard: async (): Promise<ApiResponse<FacultyDashboardData>> => {
    const response = await api.get<ApiResponse<FacultyDashboardData>>('/faculty/dashboard');
    return response.data;
  },

  getStudentDashboard: async (): Promise<ApiResponse<StudentDashboardData>> => {
    const response = await api.get<ApiResponse<StudentDashboardData>>('/student/dashboard');
    return response.data;
  },
};

export default dashboardService;
