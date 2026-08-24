import api from './api';
import { Unit, UnitRequest, PageResponse, ApiResponse } from '../types';

const unitService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<ApiResponse<PageResponse<Unit>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    const response = await api.get<ApiResponse<PageResponse<Unit>>>(
      `/units?${params.toString()}`
    );
    return response.data;
  },

  getBySubject: async (subjectId: number): Promise<ApiResponse<Unit[]>> => {
    const response = await api.get<ApiResponse<Unit[]>>(`/units/subject/${subjectId}`);
    return response.data;
  },

  create: async (data: UnitRequest): Promise<ApiResponse<Unit>> => {
    const response = await api.post<ApiResponse<Unit>>('/units', data);
    return response.data;
  },

  update: async (id: number, data: UnitRequest): Promise<ApiResponse<Unit>> => {
    const response = await api.put<ApiResponse<Unit>>(`/units/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/units/${id}`);
    return response.data;
  },
};

export default unitService;
