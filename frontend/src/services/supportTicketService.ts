import api from './api';
import { SupportTicket, PageResponse, ApiResponse } from '../types';

const supportTicketService = {
  getAll: async (): Promise<ApiResponse<PageResponse<SupportTicket>>> => {
    const response = await api.get<ApiResponse<PageResponse<SupportTicket>>>('/support-tickets');
    return response.data;
  },
  getById: async (id: number): Promise<ApiResponse<SupportTicket>> => {
    const response = await api.get<ApiResponse<SupportTicket>>(`/support-tickets/${id}`);
    return response.data;
  },
  create: async (data: any): Promise<ApiResponse<SupportTicket>> => {
    const response = await api.post<ApiResponse<SupportTicket>>('/support-tickets', data);
    return response.data;
  },
  update: async (id: number, data: any): Promise<ApiResponse<SupportTicket>> => {
    const response = await api.put<ApiResponse<SupportTicket>>(`/support-tickets/${id}`, data);
    return response.data;
  },
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/support-tickets/${id}`);
    return response.data;
  },
  getMyTickets: async (): Promise<ApiResponse<PageResponse<SupportTicket>>> => {
    const response = await api.get<ApiResponse<PageResponse<SupportTicket>>>('/support-tickets/my');
    return response.data;
  },
};

export default supportTicketService;
