import api from './api';

const adaptiveService = {
  start: (subjectId: number) => api.post('/adaptive/start', { subjectId }),
  getNext: (sessionId: number) => api.get(`/adaptive/${sessionId}/next`),
  submitAnswer: (sessionId: number, data: any) => api.post(`/adaptive/${sessionId}/answer`, data),
  endSession: (sessionId: number) => api.post(`/adaptive/${sessionId}/end`),
  getHistory: (sessionId: number) => api.get(`/adaptive/${sessionId}/history`),
};

export default adaptiveService;
