import api from './api';
import { ExamResult, PageResponse, ApiResponse } from '../types';

const examResultService = {
  getExamResults: async (examId: number, page: number = 0, size: number = 10): Promise<ApiResponse<PageResponse<ExamResult>>> => {
    const response = await api.get<ApiResponse<PageResponse<ExamResult>>>(`/exam-results/exam/${examId}`, { params: { page, size } });
    return response.data;
  },

  getStudentResults: async (studentId: number, page: number = 0, size: number = 10): Promise<ApiResponse<PageResponse<ExamResult>>> => {
    const response = await api.get<ApiResponse<PageResponse<ExamResult>>>(`/exam-results/student/${studentId}`, { params: { page, size } });
    return response.data;
  },

  getResult: async (examId: number, studentId: number): Promise<ApiResponse<ExamResult>> => {
    const response = await api.get<ApiResponse<ExamResult>>(`/exam-results/exam/${examId}/student/${studentId}`);
    return response.data;
  },

  getResultDetails: async (resultId: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/exam-results/${resultId}/details`);
    return response.data;
  },

  exportResults: async (examId: number): Promise<any> => {
    const response = await api.get(`/exam-results/exam/${examId}/export`, { responseType: 'blob' });
    return response.data;
  },
};

export default examResultService;
