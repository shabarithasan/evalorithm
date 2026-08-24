import api from './api';
import { Student, StudentRequest, PageResponse, ApiResponse } from '../types';

const studentService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<ApiResponse<PageResponse<Student>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    const response = await api.get<ApiResponse<PageResponse<Student>>>(
      `/students?${params.toString()}`
    );
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<Student>> => {
    const response = await api.get<ApiResponse<Student>>(`/students/${id}`);
    return response.data;
  },

  create: async (data: StudentRequest): Promise<ApiResponse<Student>> => {
    const response = await api.post<ApiResponse<Student>>('/students', data);
    return response.data;
  },

  update: async (id: number, data: StudentRequest): Promise<ApiResponse<Student>> => {
    const response = await api.put<ApiResponse<Student>>(`/students/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/students/${id}`);
    return response.data;
  },

  enrollSubjects: async (id: number, subjectIds: number[]): Promise<ApiResponse<Student>> => {
    const response = await api.post<ApiResponse<Student>>(`/students/${id}/subjects`, { subjectIds });
    return response.data;
  },

  unenrollSubjects: async (id: number, subjectIds: number[]): Promise<ApiResponse<Student>> => {
    const response = await api.delete<ApiResponse<Student>>(`/students/${id}/subjects`, {
      data: { subjectIds },
    });
    return response.data;
  },
};

export default studentService;
