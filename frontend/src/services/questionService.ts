import api from './api';
import {
  Question,
  QuestionRequest,
  QuestionDashboardData,
  QuestionVersion,
  PageResponse,
  ApiResponse,
  BulkImportResult,
} from '../types';

const questionService = {
  getAll: async (params: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
    departmentId?: number;
    semesterId?: number;
    subjectId?: number;
    unitId?: number;
    topicId?: number;
    questionType?: string;
    difficulty?: string;
    bloomLevel?: string;
    status?: string;
    categoryId?: number;
    searchTerm?: string;
    isArchived?: boolean;
  } = {}): Promise<ApiResponse<PageResponse<Question>>> => {
    const searchParams = new URLSearchParams();
    if (params.page !== undefined) searchParams.append('page', params.page.toString());
    if (params.size !== undefined) searchParams.append('size', params.size.toString());
    if (params.sortBy) searchParams.append('sortBy', params.sortBy);
    if (params.sortDir) searchParams.append('sortDir', params.sortDir);
    if (params.departmentId) searchParams.append('departmentId', params.departmentId.toString());
    if (params.semesterId) searchParams.append('semesterId', params.semesterId.toString());
    if (params.subjectId) searchParams.append('subjectId', params.subjectId.toString());
    if (params.unitId) searchParams.append('unitId', params.unitId.toString());
    if (params.topicId) searchParams.append('topicId', params.topicId.toString());
    if (params.questionType) searchParams.append('questionType', params.questionType);
    if (params.difficulty) searchParams.append('difficulty', params.difficulty);
    if (params.bloomLevel) searchParams.append('bloomLevel', params.bloomLevel);
    if (params.status) searchParams.append('status', params.status);
    if (params.categoryId) searchParams.append('categoryId', params.categoryId.toString());
    if (params.searchTerm) searchParams.append('searchTerm', params.searchTerm);
    if (params.isArchived !== undefined) searchParams.append('isArchived', params.isArchived.toString());
    const response = await api.get<ApiResponse<PageResponse<Question>>>(
      `/questions?${searchParams.toString()}`
    );
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<Question>> => {
    const response = await api.get<ApiResponse<Question>>(`/questions/${id}`);
    return response.data;
  },

  create: async (data: QuestionRequest): Promise<ApiResponse<Question>> => {
    const response = await api.post<ApiResponse<Question>>('/questions', data);
    return response.data;
  },

  update: async (id: number, data: QuestionRequest): Promise<ApiResponse<Question>> => {
    const response = await api.put<ApiResponse<Question>>(`/questions/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/questions/${id}`);
    return response.data;
  },

  duplicate: async (id: number): Promise<ApiResponse<Question>> => {
    const response = await api.post<ApiResponse<Question>>(`/questions/${id}/duplicate`);
    return response.data;
  },

  archive: async (id: number): Promise<ApiResponse<Question>> => {
    const response = await api.put<ApiResponse<Question>>(`/questions/${id}/archive`);
    return response.data;
  },

  restore: async (id: number): Promise<ApiResponse<Question>> => {
    const response = await api.put<ApiResponse<Question>>(`/questions/${id}/restore`);
    return response.data;
  },

  submitForReview: async (id: number): Promise<ApiResponse<Question>> => {
    const response = await api.put<ApiResponse<Question>>(`/questions/${id}/submit-review`);
    return response.data;
  },

  approve: async (id: number, data: { status: string; comments: string }): Promise<ApiResponse<Question>> => {
    const response = await api.put<ApiResponse<Question>>(`/questions/${id}/approve`, data);
    return response.data;
  },

  getVersions: async (id: number): Promise<ApiResponse<QuestionVersion[]>> => {
    const response = await api.get<ApiResponse<QuestionVersion[]>>(`/questions/${id}/versions`);
    return response.data;
  },

  getDashboard: async (): Promise<ApiResponse<QuestionDashboardData>> => {
    const response = await api.get<ApiResponse<QuestionDashboardData>>('/questions/dashboard');
    return response.data;
  },
};

export default questionService;
