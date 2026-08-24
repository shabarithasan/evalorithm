import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Rating,
  Chip,
  Alert,
} from '@mui/material';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { feedbackService } from '../../services';
import { Feedback } from '../../types';

const FacultyFeedbackPage: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchFeedback();
  }, []);

  const fetchFeedback = async () => {
    try {
      const response = await feedbackService.getMyFeedback();
      if (response.success) {
        setFeedbacks(response.data?.content || response.data || []);
      }
    } catch {
      setError('Failed to load feedback');
    } finally {
      setLoading(false);
    }
  };

  const averageRating = feedbacks.length > 0
    ? feedbacks.reduce((sum, f) => sum + f.rating, 0) / feedbacks.length
    : 0;

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="My Feedback" subtitle="View feedback received from students" />

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card variant="outlined" sx={{ mb: 3 }}>
        <CardContent sx={{ textAlign: 'center' }}>
          <Typography variant="h3" fontWeight={700} color="primary.main">
            {averageRating.toFixed(1)}
          </Typography>
          <Rating value={averageRating} readOnly size="large" sx={{ my: 1 }} />
          <Typography variant="body2" color="text.secondary">
            Based on {feedbacks.length} feedback{feedbacks.length !== 1 ? 's' : ''}
          </Typography>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>All Feedback</Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>From</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Rating</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Comment</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Suggestions</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Date</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {feedbacks.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      <Typography color="text.secondary" sx={{ py: 3 }}>
                        No feedback received yet.
                      </Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  feedbacks.map((fb) => (
                    <TableRow key={fb.id} hover>
                      <TableCell>
                        {fb.isAnonymous ? (
                          <Chip label="Anonymous" size="small" variant="outlined" />
                        ) : (
                          fb.fromUserName
                        )}
                      </TableCell>
                      <TableCell><Rating value={fb.rating} readOnly size="small" /></TableCell>
                      <TableCell>{fb.comment}</TableCell>
                      <TableCell>{fb.suggestions || '-'}</TableCell>
                      <TableCell>{fb.subjectName || '-'}</TableCell>
                      <TableCell>{new Date(fb.createdAt).toLocaleDateString()}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>
    </Box>
  );
};

export default FacultyFeedbackPage;
