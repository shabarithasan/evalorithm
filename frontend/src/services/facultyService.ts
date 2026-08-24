import api from './api';
import { Faculty, FacultyRequest, PageResponse, ApiResponse } from '../types';

const facultyService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<ApiResponse<PageResponse<Faculty>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    const response = await api.get<ApiResponse<PageResponse<Faculty>>>(
      `/faculty?${params.toString()}`
    );
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<Faculty>> => {
    const response = await api.get<ApiResponse<Faculty>>(`/faculty/${id}`);
    return response.data;
  },

  create: async (data: FacultyRequest): Promise<ApiResponse<Faculty>> => {
    const response = await api.post<ApiResponse<Faculty>>('/faculty', data);
    return response.data;
  },

  update: async (id: number, data: FacultyRequest): Promise<ApiResponse<Faculty>> => {
    const response = await api.put<ApiResponse<Faculty>>(`/faculty/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/faculty/${id}`);
    return response.data;
  },

  assignSubjects: async (id: number, subjectIds: number[]): Promise<ApiResponse<Faculty>> => {
    const response = await api.post<ApiResponse<Faculty>>(`/faculty/${id}/subjects`, { subjectIds });
    return response.data;
  },

  removeSubjects: async (id: number, subjectIds: number[]): Promise<ApiResponse<Faculty>> => {
    const response = await api.delete<ApiResponse<Faculty>>(`/faculty/${id}/subjects`, {
      data: { subjectIds },
    });
    return response.data;
  },
};

export default facultyService;
