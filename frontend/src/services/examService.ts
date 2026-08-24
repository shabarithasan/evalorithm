import api from './api';
import { Exam, ExamRequest, ExamDashboardData, PageResponse, ApiResponse } from '../types';

const examService = {
  getAll: async (params: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    examType?: string;
    departmentId?: number;
  }): Promise<ApiResponse<PageResponse<Exam>>> => {
    const queryParams: any = {};
    if (params.page !== undefined) queryParams.page = params.page;
    if (params.size !== undefined) queryParams.size = params.size;
    if (params.search) queryParams.search = params.search;
    if (params.status) queryParams.status = params.status;
    if (params.examType) queryParams.examType = params.examType;
    if (params.departmentId) queryParams.departmentId = params.departmentId;
    const response = await api.get<ApiResponse<PageResponse<Exam>>>('/exams', { params: queryParams });
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<Exam>> => {
    const response = await api.get<ApiResponse<Exam>>(`/exams/${id}`);
    return response.data;
  },

  create: async (data: ExamRequest): Promise<ApiResponse<Exam>> => {
    const response = await api.post<ApiResponse<Exam>>('/exams', data);
    return response.data;
  },

  update: async (id: number, data: Partial<ExamRequest>): Promise<ApiResponse<Exam>> => {
    const response = await api.put<ApiResponse<Exam>>(`/exams/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/exams/${id}`);
    return response.data;
  },

  clone: async (id: number): Promise<ApiResponse<Exam>> => {
    const response = await api.post<ApiResponse<Exam>>(`/exams/${id}/clone`);
    return response.data;
  },

  publish: async (id: number): Promise<ApiResponse<Exam>> => {
    const response = await api.put<ApiResponse<Exam>>(`/exams/${id}/publish`);
    return response.data;
  },

  archive: async (id: number): Promise<ApiResponse<Exam>> => {
    const response = await api.put<ApiResponse<Exam>>(`/exams/${id}/archive`);
    return response.data;
  },

  cancel: async (id: number): Promise<ApiResponse<Exam>> => {
    const response = await api.put<ApiResponse<Exam>>(`/exams/${id}/cancel`);
    return response.data;
  },

  addQuestions: async (id: number, questions: { questionId: number; marks: number; orderNumber: number }[]): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>(`/exams/${id}/questions`, questions);
    return response.data;
  },

  removeQuestion: async (examId: number, questionId: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/exams/${examId}/questions/${questionId}`);
    return response.data;
  },

  assignStudents: async (id: number, studentIds: number[]): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>(`/exams/${id}/assign-students`, studentIds);
    return response.data;
  },

  unassignStudents: async (id: number, studentIds: number[]): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>(`/exams/${id}/unassign-students`, studentIds);
    return response.data;
  },

  getDashboard: async (): Promise<ApiResponse<ExamDashboardData>> => {
    const response = await api.get<ApiResponse<ExamDashboardData>>('/exams/dashboard');
    return response.data;
  },

  getAssignedStudents: async (id: number, page: number = 0, size: number = 10): Promise<ApiResponse<PageResponse<any>>> => {
    const response = await api.get<ApiResponse<PageResponse<any>>>(`/exams/${id}/students`, { params: { page, size } });
    return response.data;
  },

  getExamQuestions: async (id: number): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>(`/exams/${id}/questions`);
    return response.data;
  },

  getAttendance: async (id: number): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>(`/exams/${id}/attendance`);
    return response.data;
  },

  getReport: async (id: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/exams/${id}/report`);
    return response.data;
  },

  startExam: async (examId: number): Promise<ApiResponse<{ attemptId: number }>> => {
    const response = await api.post<ApiResponse<{ attemptId: number }>>(`/exam-session/start?examId=${examId}`);
    return response.data;
  },

  submitExam: async (attemptId: number): Promise<ApiResponse<{ totalCorrect: number; totalWrong: number; totalSkipped: number }>> => {
    const response = await api.post<ApiResponse<{ totalCorrect: number; totalWrong: number; totalSkipped: number }>>(`/exam-session/${attemptId}/submit`);
    return response.data;
  },

  getSubjects: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/subjects?size=100');
    return response.data;
  },

  getDepartments: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/departments?size=100');
    return response.data;
  },

  getSemesters: async (): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>('/semesters?size=100');
    return response.data;
  },
};

export default examService;
