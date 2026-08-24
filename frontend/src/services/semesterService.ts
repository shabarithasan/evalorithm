import api from './api';
import { Semester, SemesterRequest, PageResponse, ApiResponse } from '../types';

const semesterService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<ApiResponse<PageResponse<Semester>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    const response = await api.get<ApiResponse<PageResponse<Semester>>>(
      `/semesters?${params.toString()}`
    );
    return response.data;
  },

  getByDepartment: async (departmentId: number): Promise<ApiResponse<Semester[]>> => {
    const response = await api.get<ApiResponse<Semester[]>>(
      `/semesters/department/${departmentId}`
    );
    return response.data;
  },

  create: async (data: SemesterRequest): Promise<ApiResponse<Semester>> => {
    const response = await api.post<ApiResponse<Semester>>('/semesters', data);
    return response.data;
  },

  update: async (id: number, data: SemesterRequest): Promise<ApiResponse<Semester>> => {
    const response = await api.put<ApiResponse<Semester>>(`/semesters/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/semesters/${id}`);
    return response.data;
  },
};

export default semesterService;
