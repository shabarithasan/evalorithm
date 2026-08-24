import api from './api';
import { QuestionMedia, ApiResponse } from '../types';

const questionMediaService = {
  getMedia: async (questionId: number): Promise<ApiResponse<QuestionMedia[]>> => {
    const response = await api.get<ApiResponse<QuestionMedia[]>>(`/questions/${questionId}/media`);
    return response.data;
  },

  uploadMedia: async (questionId: number, file: File): Promise<ApiResponse<QuestionMedia>> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post<ApiResponse<QuestionMedia>>(`/questions/${questionId}/media`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  deleteMedia: async (questionId: number, mediaId: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/questions/${questionId}/media/${mediaId}`);
    return response.data;
  },
};

export default questionMediaService;
