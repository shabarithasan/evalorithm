import api from './api';
import { ExamReport, ApiResponse } from '../types';

const examReportService = {
  getExamReport: async (examId: number): Promise<ApiResponse<ExamReport>> => {
    const response = await api.get<ApiResponse<ExamReport>>(`/exam-reports/exam/${examId}`);
    return response.data;
  },

  getQuestionWiseReport: async (examId: number): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>(`/exam-reports/exam/${examId}/question-wise`);
    return response.data;
  },

  getSubjectWiseReport: async (departmentId: number): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>(`/exam-reports/department/${departmentId}/subject-wise`);
    return response.data;
  },
};

export default examReportService;
