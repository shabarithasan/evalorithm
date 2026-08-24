import api from './api';
import { QuestionCategory, QuestionCategoryRequest, PageResponse, ApiResponse, Status } from '../types';

const questionCategoryService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc',
    search?: string
  ): Promise<ApiResponse<PageResponse<QuestionCategory>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    if (search) params.append('search', search);
    const response = await api.get<ApiResponse<PageResponse<QuestionCategory>>>(
      `/question-categories?${params.toString()}`
    );
    return response.data;
  },

  getAllActive: async (): Promise<ApiResponse<QuestionCategory[]>> => {
    const response = await api.get<ApiResponse<QuestionCategory[]>>('/question-categories/active');
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<QuestionCategory>> => {
    const response = await api.get<ApiResponse<QuestionCategory>>(`/question-categories/${id}`);
    return response.data;
  },

  create: async (data: QuestionCategoryRequest): Promise<ApiResponse<QuestionCategory>> => {
    const response = await api.post<ApiResponse<QuestionCategory>>('/question-categories', data);
    return response.data;
  },

  update: async (id: number, data: QuestionCategoryRequest): Promise<ApiResponse<QuestionCategory>> => {
    const response = await api.put<ApiResponse<QuestionCategory>>(`/question-categories/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/question-categories/${id}`);
    return response.data;
  },
};

export default questionCategoryService;
