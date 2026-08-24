import api from './api';
import { BulkImportResult, ApiResponse } from '../types';

const bulkImportService = {
  importFromExcel: async (file: File): Promise<ApiResponse<BulkImportResult>> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post<ApiResponse<BulkImportResult>>('/questions/import/excel', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  importFromCsv: async (file: File): Promise<ApiResponse<BulkImportResult>> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post<ApiResponse<BulkImportResult>>('/questions/import/csv', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
};

export default bulkImportService;
