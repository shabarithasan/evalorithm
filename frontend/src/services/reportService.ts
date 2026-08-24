import api from './api';

const reportService = {
  generate: (data: any) => api.post('/reports/generate', data, { responseType: 'blob' }),
  getHistory: () => api.get('/reports/history'),
  download: (reportId: string) => api.get(`/reports/${reportId}/download`, { responseType: 'blob' }),
  getAvailableTypes: () => api.get('/reports/types'),
};

export default reportService;
