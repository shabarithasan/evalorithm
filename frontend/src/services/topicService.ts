import api from './api';
import { Topic, TopicRequest, PageResponse, ApiResponse } from '../types';

const topicService = {
  getAll: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<ApiResponse<PageResponse<Topic>>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('sortDir', sortDir);
    const response = await api.get<ApiResponse<PageResponse<Topic>>>(
      `/topics?${params.toString()}`
    );
    return response.data;
  },

  getByUnit: async (unitId: number): Promise<ApiResponse<Topic[]>> => {
    const response = await api.get<ApiResponse<Topic[]>>(`/topics/unit/${unitId}`);
    return response.data;
  },

  create: async (data: TopicRequest): Promise<ApiResponse<Topic>> => {
    const response = await api.post<ApiResponse<Topic>>('/topics', data);
    return response.data;
  },

  update: async (id: number, data: TopicRequest): Promise<ApiResponse<Topic>> => {
    const response = await api.put<ApiResponse<Topic>>(`/topics/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/topics/${id}`);
    return response.data;
  },
};

export default topicService;
