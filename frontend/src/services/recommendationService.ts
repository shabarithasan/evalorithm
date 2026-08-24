import api from './api';

const recommendationService = {
  getStudentRecommendations: (studentId: number) => api.get(`/recommendations/student/${studentId}`),
  markAsRead: (id: number) => api.put(`/recommendations/${id}/read`),
  accept: (id: number) => api.put(`/recommendations/${id}/accept`),
};

export default recommendationService;
