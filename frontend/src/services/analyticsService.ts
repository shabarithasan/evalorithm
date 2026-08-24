import api from './api';
import { FacultyAnalyticsData, StudentAnalytics, ApiResponse, LeaderboardItem, SubjectPerformanceItem, AccuracyTrend, UnitPerformanceItem, TopicPerformanceItem } from '../types';

const analyticsService = {
  getStudentDashboard: async (studentId: number): Promise<ApiResponse<StudentAnalytics>> => {
    const response = await api.get<ApiResponse<StudentAnalytics>>(`/analytics/students/${studentId}/dashboard`);
    return response.data;
  },
  getSubjectPerformance: async (studentId: number): Promise<ApiResponse<SubjectPerformanceItem[]>> => {
    const response = await api.get<ApiResponse<SubjectPerformanceItem[]>>(`/analytics/students/${studentId}/subjects`);
    return response.data;
  },
  getUnitPerformance: async (studentId: number, subjectId: number): Promise<ApiResponse<UnitPerformanceItem[]>> => {
    const response = await api.get<ApiResponse<UnitPerformanceItem[]>>(`/analytics/students/${studentId}/subjects/${subjectId}/units`);
    return response.data;
  },
  getTopicPerformance: async (studentId: number, subjectId: number): Promise<ApiResponse<TopicPerformanceItem[]>> => {
    const response = await api.get<ApiResponse<TopicPerformanceItem[]>>(`/analytics/students/${studentId}/subjects/${subjectId}/topics`);
    return response.data;
  },
  getDifficultyPerformance: async (studentId: number): Promise<ApiResponse<Record<string, number>>> => {
    const response = await api.get<ApiResponse<Record<string, number>>>(`/analytics/students/${studentId}/difficulty`);
    return response.data;
  },
  getAccuracyTrend: async (studentId: number): Promise<ApiResponse<AccuracyTrend[]>> => {
    const response = await api.get<ApiResponse<AccuracyTrend[]>>(`/analytics/students/${studentId}/accuracy-trend`);
    return response.data;
  },
  calculateAnalytics: async (studentId: number): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/analytics/students/${studentId}/calculate`);
    return response.data;
  },

  getFacultyDashboard: async (facultyId: number): Promise<ApiResponse<FacultyAnalyticsData[]>> => {
    const response = await api.get<ApiResponse<FacultyAnalyticsData[]>>(`/analytics/faculty/${facultyId}/dashboard`);
    return response.data;
  },
  getClassPerformance: async (facultyId: number, subjectId: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/analytics/faculty/${facultyId}/subjects/${subjectId}/class-performance`);
    return response.data;
  },
  getTopPerformers: async (facultyId: number, subjectId: number = 0): Promise<ApiResponse<LeaderboardItem[]>> => {
    const response = await api.get<ApiResponse<LeaderboardItem[]>>(`/analytics/faculty/${facultyId}/subjects/${subjectId}/top-performers`);
    return response.data;
  },
  getLowPerformers: async (facultyId: number, subjectId: number = 0): Promise<ApiResponse<LeaderboardItem[]>> => {
    const response = await api.get<ApiResponse<LeaderboardItem[]>>(`/analytics/faculty/${facultyId}/subjects/${subjectId}/low-performers`);
    return response.data;
  },

  getAdminOverview: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/analytics/admin/overview');
    return response.data;
  },
  getDepartmentPerformance: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/analytics/admin/departments');
    return response.data;
  },
  getStudentGrowth: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/analytics/admin/student-growth');
    return response.data;
  },
  getTopPerformersGlobal: async (): Promise<ApiResponse<LeaderboardItem[]>> => {
    const response = await api.get<ApiResponse<LeaderboardItem[]>>('/analytics/admin/top-performers');
    return response.data;
  },
  getLowPerformersGlobal: async (): Promise<ApiResponse<LeaderboardItem[]>> => {
    const response = await api.get<ApiResponse<LeaderboardItem[]>>('/analytics/admin/low-performers');
    return response.data;
  },
  getFacultyPerformance: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/analytics/admin/faculty-performance');
    return response.data;
  },
};

export default analyticsService;
