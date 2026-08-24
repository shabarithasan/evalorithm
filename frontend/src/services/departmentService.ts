import api from './api';
import { Department, DepartmentRequest, PageResponse, ApiResponse } from '../types';

const departmentService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc',
    search?: string
  ): Promise<ApiResponse<PageResponse<Department>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    if (search) params.append('search', search);
    const response = await api.get<ApiResponse<PageResponse<Department>>>(
      `/departments?${params.toString()}`
    );
    return response.data;
  },

  getById: async (id: number): Promise<ApiResponse<Department>> => {
    const response = await api.get<ApiResponse<Department>>(`/departments/${id}`);
    return response.data;
  },

  create: async (data: DepartmentRequest): Promise<ApiResponse<Department>> => {
    const response = await api.post<ApiResponse<Department>>('/departments', data);
    return response.data;
  },

  update: async (id: number, data: DepartmentRequest): Promise<ApiResponse<Department>> => {
    const response = await api.put<ApiResponse<Department>>(`/departments/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/departments/${id}`);
    return response.data;
  },
};

export default departmentService;
