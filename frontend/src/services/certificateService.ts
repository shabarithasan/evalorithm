import api from './api';
import { Certificate, ApiResponse, PageResponse } from '../types';

const certificateService = {
  generate: async (data: any): Promise<ApiResponse<Certificate>> => {
    const response = await api.post<ApiResponse<Certificate>>('/certificates/generate', data);
    return response.data;
  },
  getAll: async (): Promise<ApiResponse<PageResponse<Certificate>>> => {
    const response = await api.get<ApiResponse<PageResponse<Certificate>>>('/certificates');
    return response.data;
  },
  getStudentCerts: async (studentId: number): Promise<ApiResponse<PageResponse<Certificate>>> => {
    const response = await api.get<ApiResponse<PageResponse<Certificate>>>(`/certificates/student/${studentId}`);
    return response.data;
  },
  getMyCerts: async (): Promise<ApiResponse<PageResponse<Certificate>>> => {
    const response = await api.get<ApiResponse<PageResponse<Certificate>>>('/certificates/my');
    return response.data;
  },
  verify: async (certNumber: string): Promise<ApiResponse<Certificate>> => {
    const response = await api.get<ApiResponse<Certificate>>(`/certificates/verify/${certNumber}`);
    return response.data;
  },
  download: (id: number) => api.get(`/certificates/${id}/download`, { responseType: 'blob' }),
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/certificates/${id}`);
    return response.data;
  },
};

export default certificateService;
