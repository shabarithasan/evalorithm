import api from './api';
import { Feedback, PageResponse, ApiResponse } from '../types';

const feedbackService = {
  getAll: async (): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const response = await api.get<ApiResponse<PageResponse<Feedback>>>('/feedback');
    return response.data;
  },
  getById: async (id: number): Promise<ApiResponse<Feedback>> => {
    const response = await api.get<ApiResponse<Feedback>>(`/feedback/${id}`);
    return response.data;
  },
  create: async (data: any): Promise<ApiResponse<Feedback>> => {
    const response = await api.post<ApiResponse<Feedback>>('/feedback', data);
    return response.data;
  },
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/feedback/${id}`);
    return response.data;
  },
  getByType: async (type: string): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const response = await api.get<ApiResponse<PageResponse<Feedback>>>(`/feedback/type/${type}`);
    return response.data;
  },
  getBySubject: async (subjectId: number): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const response = await api.get<ApiResponse<PageResponse<Feedback>>>(`/feedback/subject/${subjectId}`);
    return response.data;
  },
  getMyFeedback: async (): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const response = await api.get<ApiResponse<PageResponse<Feedback>>>('/feedback/my');
    return response.data;
  },
  getAnalytics: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/feedback/analytics');
    return response.data;
  },
};

export default feedbackService;
