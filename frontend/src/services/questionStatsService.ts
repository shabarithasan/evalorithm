import api from './api';
import { QuestionStatistics, ApiResponse } from '../types';

const questionStatsService = {
  getStatistics: async (questionId: number): Promise<ApiResponse<QuestionStatistics>> => {
    const response = await api.get<ApiResponse<QuestionStatistics>>(`/questions/${questionId}/statistics`);
    return response.data;
  },

  recordView: async (questionId: number): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>(`/questions/${questionId}/statistics/view`);
    return response.data;
  },

  recordUsage: async (questionId: number, correct: boolean): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>(`/questions/${questionId}/statistics/usage`, { correct });
    return response.data;
  },
};

export default questionStatsService;
