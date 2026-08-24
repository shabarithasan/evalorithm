import api from './api';
import { ApiResponse } from '../types';

const examTakingService = {
  startExam: async (examId: number): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/exam-session/start?examId=${examId}`);
    return response.data;
  },

  getQuestion: async (attemptId: number, index: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/exam-session/${attemptId}/question/${index}`);
    return response.data;
  },

  saveAnswer: async (attemptId: number, data: any): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/exam-session/${attemptId}/save-answer`, data);
    return response.data;
  },

  submitExam: async (attemptId: number): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/exam-session/${attemptId}/submit`);
    return response.data;
  },

  resumeExam: async (attemptId: number): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>(`/exam-session/${attemptId}/resume`);
    return response.data;
  },

  getExamStatus: async (examId: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/exam-session/${examId}/status`);
    return response.data;
  },
};

export default examTakingService;
