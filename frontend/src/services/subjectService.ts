import api from './api';
import { Subject, SubjectRequest, PageResponse, ApiResponse } from '../types';

const subjectService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc',
    search?: string
  ): Promise<ApiResponse<PageResponse<Subject>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    if (search) params.append('search', search);
    const response = await api.get<ApiResponse<PageResponse<Subject>>>(
      `/subjects?${params.toString()}`
    );
    return response.data;
  },

  getByDepartment: async (departmentId: number): Promise<ApiResponse<Subject[]>> => {
    const response = await api.get<ApiResponse<Subject[]>>(
      `/subjects/department/${departmentId}`
    );
    return response.data;
  },

  getBySemester: async (semesterId: number): Promise<ApiResponse<Subject[]>> => {
    const response = await api.get<ApiResponse<Subject[]>>(
      `/subjects/semester/${semesterId}`
    );
    return response.data;
  },

  create: async (data: SubjectRequest): Promise<ApiResponse<Subject>> => {
    const response = await api.post<ApiResponse<Subject>>('/subjects', data);
    return response.data;
  },

  update: async (id: number, data: SubjectRequest): Promise<ApiResponse<Subject>> => {
    const response = await api.put<ApiResponse<Subject>>(`/subjects/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/subjects/${id}`);
    return response.data;
  },
};

export default subjectService;
