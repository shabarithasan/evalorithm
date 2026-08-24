import api from './api';
import { ExamNotification, ApiResponse } from '../types';

const examNotificationService = {
  getAll: async (): Promise<ApiResponse<ExamNotification[]>> => {
    const response = await api.get<ApiResponse<ExamNotification[]>>('/exam-notifications');
    return response.data;
  },

  markAsRead: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.put<ApiResponse<void>>(`/exam-notifications/${id}/read`);
    return response.data;
  },

  markAllAsRead: async (): Promise<ApiResponse<void>> => {
    const response = await api.put<ApiResponse<void>>('/exam-notifications/read-all');
    return response.data;
  },
};

export default examNotificationService;
