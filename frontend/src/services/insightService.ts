import api from './api';

const insightService = {
  getStudentInsights: (studentId: number) => api.get(`/insights/student/${studentId}`),
  getSmartNotifications: (studentId: number) => api.get(`/insights/student/${studentId}/notifications`),
};

export default insightService;
