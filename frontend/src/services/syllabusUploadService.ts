import api from './api';
import { SyllabusUploadResult, ApiResponse } from '../types';

const syllabusUploadService = {
  uploadSyllabus: async (file: File, departmentId: number, semesterId: number, subjectId: number): Promise<ApiResponse<SyllabusUploadResult>> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('departmentId', String(departmentId));
    formData.append('semesterId', String(semesterId));
    formData.append('subjectId', String(subjectId));
    const response = await api.post<ApiResponse<SyllabusUploadResult>>('/syllabus-upload/upload', formData);
    return response.data;
  },
};

export default syllabusUploadService;
