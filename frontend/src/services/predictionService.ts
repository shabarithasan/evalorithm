import api from './api';

const predictionService = {
  getStudentPredictions: (studentId: number) => api.get(`/predictions/student/${studentId}`),
  generate: (studentId: number) => api.post(`/predictions/student/${studentId}/generate`),
};

export default predictionService;
