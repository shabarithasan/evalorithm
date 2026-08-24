import api from './api';
import { AIQuestionGenerateRequest } from '../types';

const aiQuestionService = {
  generate: (data: AIQuestionGenerateRequest) => api.post('/ai/questions/generate', data),
  getAll: (params: { page?: number; size?: number; subjectId?: number; approved?: boolean }) =>
    api.get('/ai/questions', { params }),
  approve: (id: number) => api.put(`/ai/questions/${id}/approve`),
  reject: (id: number) => api.put(`/ai/questions/${id}/reject`),
  getDashboard: () => api.get('/ai/questions/dashboard'),
};

export default aiQuestionService;
