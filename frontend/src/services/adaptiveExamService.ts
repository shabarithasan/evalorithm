import api from './api';
import { ApiResponse } from '../types';

export interface AdaptiveExamRequest {
  subjectId: number;
  departmentId: number;
  semesterId: number;
  totalQuestions?: number;
  createdBy: number;
}

export interface AdaptiveQuestion {
  examQuestionId: number;
  orderNumber: number;
  questionTitle: string;
  questionDescription: string;
  questionType: string;
  marks: number;
  difficulty: string;
  options?: Array<{ optionLabel: string; optionText: string }>;
}

export interface AdaptiveExamResponse {
  examId: number;
  title: string;
  totalQuestions: number;
  durationMinutes: number;
  message: string;
}

const adaptiveExamService = {
  createAdaptiveExam: async (request: AdaptiveExamRequest): Promise<ApiResponse<AdaptiveExamResponse>> => {
    const response = await api.post<ApiResponse<AdaptiveExamResponse>>('/adaptive-exam/create', request);
    return response.data;
  },

  getNextQuestion: async (attemptId: number, previousCorrect: boolean, previousQuestionId?: number): Promise<ApiResponse<AdaptiveQuestion>> => {
    const params = new URLSearchParams();
    params.append('previousCorrect', String(previousCorrect));
    if (previousQuestionId) {
      params.append('previousQuestionId', String(previousQuestionId));
    }
    const response = await api.get<ApiResponse<AdaptiveQuestion>>(`/adaptive-exam/next-question/${attemptId}?${params.toString()}`);
    return response.data;
  },
};

export default adaptiveExamService;