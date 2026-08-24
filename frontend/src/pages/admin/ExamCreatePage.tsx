import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Snackbar, Alert } from '@mui/material';
import ExamForm from '../../components/exam/ExamForm';
import { examService } from '../../services';
import { ExamRequest } from '../../types';

const ExamCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false, message: '', severity: 'success',
  });

  const handleSubmit = async (data: ExamRequest) => {
    setLoading(true);
    try {
      const res = await examService.create(data);
      if (res.success) {
        setSnackbar({ open: true, message: 'Exam created successfully', severity: 'success' });
        setTimeout(() => navigate('/admin/exams'), 1500);
      }
    } catch (err: any) {
      setSnackbar({ open: true, message: err.response?.data?.message || 'Failed to create exam', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Create Exam</Typography>
      <ExamForm onSubmit={handleSubmit} loading={loading} />
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
        <Alert severity={snackbar.severity} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default ExamCreatePage;
