import api from './api';
import { ExamAttendance, ApiResponse } from '../types';

const examAttendanceService = {
  getByExam: async (examId: number): Promise<ApiResponse<ExamAttendance[]>> => {
    const response = await api.get<ApiResponse<ExamAttendance[]>>(`/exam-attendance/exam/${examId}`);
    return response.data;
  },

  updateStatus: async (examId: number, studentId: number, status: string): Promise<ApiResponse<ExamAttendance>> => {
    const response = await api.put<ApiResponse<ExamAttendance>>(`/exam-attendance/exam/${examId}/student/${studentId}`, { status });
    return response.data;
  },
};

export default examAttendanceService;
