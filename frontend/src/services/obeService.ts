import api from './api';
import { CourseOutcome, ProgramOutcome, ProgramSpecificOutcome, Attainment, ApiResponse, PageResponse } from '../types';

const obeService = {
  getCOs: async (subjectId: number): Promise<ApiResponse<PageResponse<CourseOutcome>>> => {
    const response = await api.get<ApiResponse<PageResponse<CourseOutcome>>>(`/co/subject/${subjectId}`);
    return response.data;
  },
  getAllCOs: async (): Promise<ApiResponse<PageResponse<CourseOutcome>>> => {
    const response = await api.get<ApiResponse<PageResponse<CourseOutcome>>>('/co');
    return response.data;
  },
  createCO: async (data: any): Promise<ApiResponse<CourseOutcome>> => {
    const response = await api.post<ApiResponse<CourseOutcome>>('/co', data);
    return response.data;
  },
  updateCO: async (id: number, data: any): Promise<ApiResponse<CourseOutcome>> => {
    const response = await api.put<ApiResponse<CourseOutcome>>(`/co/${id}`, data);
    return response.data;
  },
  deleteCO: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/co/${id}`);
    return response.data;
  },
  mapQuestion: async (data: any): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>('/co/map-question', data);
    return response.data;
  },
  removeMapping: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/co/mapping/${id}`);
    return response.data;
  },
  getMappings: async (coId: number): Promise<ApiResponse<any[]>> => {
    const response = await api.get<ApiResponse<any[]>>(`/co/${coId}/mappings`);
    return response.data;
  },
  getPOs: async (deptId: number): Promise<ApiResponse<ProgramOutcome[]>> => {
    const response = await api.get<ApiResponse<ProgramOutcome[]>>(`/po/department/${deptId}`);
    return response.data;
  },
  getAllPOs: async (): Promise<ApiResponse<PageResponse<ProgramOutcome>>> => {
    const response = await api.get<ApiResponse<PageResponse<ProgramOutcome>>>('/po');
    return response.data;
  },
  createPO: async (data: any): Promise<ApiResponse<ProgramOutcome>> => {
    const response = await api.post<ApiResponse<ProgramOutcome>>('/po', data);
    return response.data;
  },
  updatePO: async (id: number, data: any): Promise<ApiResponse<ProgramOutcome>> => {
    const response = await api.put<ApiResponse<ProgramOutcome>>(`/po/${id}`, data);
    return response.data;
  },
  deletePO: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/po/${id}`);
    return response.data;
  },
  getPSOs: async (deptId: number): Promise<ApiResponse<ProgramSpecificOutcome[]>> => {
    const response = await api.get<ApiResponse<ProgramSpecificOutcome[]>>(`/pso/department/${deptId}`);
    return response.data;
  },
  getAllPSOs: async (): Promise<ApiResponse<PageResponse<ProgramSpecificOutcome>>> => {
    const response = await api.get<ApiResponse<PageResponse<ProgramSpecificOutcome>>>('/pso');
    return response.data;
  },
  createPSO: async (data: any): Promise<ApiResponse<ProgramSpecificOutcome>> => {
    const response = await api.post<ApiResponse<ProgramSpecificOutcome>>('/pso', data);
    return response.data;
  },
  updatePSO: async (id: number, data: any): Promise<ApiResponse<ProgramSpecificOutcome>> => {
    const response = await api.put<ApiResponse<ProgramSpecificOutcome>>(`/pso/${id}`, data);
    return response.data;
  },
  deletePSO: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/pso/${id}`);
    return response.data;
  },
  calculateAttainment: async (data: any): Promise<ApiResponse<any>> => {
    const response = await api.post<ApiResponse<any>>('/attainment/calculate', data);
    return response.data;
  },
  getDashboard: async (deptId: number, year: string): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/attainment/dashboard/${deptId}/${year}`);
    return response.data;
  },
  getBySubject: async (subjectId: number, semesterId: number): Promise<ApiResponse<any>> => {
    const response = await api.get<ApiResponse<any>>(`/attainment/subject/${subjectId}/${semesterId}`);
    return response.data;
  },
  getAllAttainments: async (): Promise<ApiResponse<PageResponse<Attainment>>> => {
    const response = await api.get<ApiResponse<PageResponse<Attainment>>>('/attainment');
    return response.data;
  },
  exportReport: (deptId: number, year: string, format: string) =>
    api.get(`/attainment/export/${deptId}/${year}`, { params: { format }, responseType: 'blob' }),
};

export default obeService;
