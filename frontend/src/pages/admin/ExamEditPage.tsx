import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Box, Typography, Snackbar, Alert } from '@mui/material';
import ExamForm from '../../components/exam/ExamForm';
import LoadingScreen from '../../components/common/LoadingScreen';
import { examService } from '../../services';
import { ExamRequest } from '../../types';

const ExamEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [exam, setExam] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false, message: '', severity: 'success',
  });

  useEffect(() => {
    if (id) {
      examService.getById(Number(id)).then((res) => {
        if (res.success) setExam(res.data);
      }).finally(() => setLoading(false));
    }
  }, [id]);

  const handleSubmit = async (data: ExamRequest) => {
    if (!id) return;
    setSubmitting(true);
    try {
      const res = await examService.update(Number(id), data);
      if (res.success) {
        setSnackbar({ open: true, message: 'Exam updated successfully', severity: 'success' });
        setTimeout(() => navigate(`/admin/exams/${id}`), 1500);
      }
    } catch (err: any) {
      setSnackbar({ open: true, message: err.response?.data?.message || 'Failed to update exam', severity: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Edit Exam</Typography>
      {exam && (
        <ExamForm
          initialData={{
            title: exam.title,
            description: exam.description,
            examType: exam.examType,
            startDate: exam.startDate,
            endDate: exam.endDate,
            durationMinutes: exam.durationMinutes,
            totalMarks: exam.totalMarks,
            passingMarks: exam.passingMarks,
            maxAttempts: exam.maxAttempts,
            negativeMarksEnabled: exam.negativeMarksEnabled,
            negativeMarksValue: exam.negativeMarksValue,
            randomizeQuestions: exam.randomizeQuestions,
            randomizeOptions: exam.randomizeOptions,
            showResultsImmediately: exam.showResultsImmediately,
            autoSubmit: exam.autoSubmit,
            fullscreenRequired: exam.fullscreenRequired,
            preventTabSwitch: exam.preventTabSwitch,
            departmentId: exam.departmentId,
            semesterId: exam.semesterId,
            subjectId: exam.subjectId,
          }}
          onSubmit={handleSubmit}
          loading={submitting}
          isEdit
        />
      )}
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
        <Alert severity={snackbar.severity} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default ExamEditPage;
