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
  Chip,
  Grid,
  Rating,
  Alert,
  TextField,
  MenuItem,
  Divider,
} from '@mui/material';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { feedbackService } from '../../services';
import { Feedback, FeedbackTypeValue } from '../../types';

const FeedbackPage: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [typeFilter, setTypeFilter] = useState<string>('ALL');
  const [analytics, setAnalytics] = useState<any>(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [fbRes, analyticsRes] = await Promise.allSettled([
        feedbackService.getAll(),
        feedbackService.getAnalytics(),
      ]);
      if (fbRes.status === 'fulfilled' && fbRes.value.success) {
        setFeedbacks(fbRes.value.data?.content || fbRes.value.data || []);
      }
      if (analyticsRes.status === 'fulfilled' && analyticsRes.value.success) {
        setAnalytics(analyticsRes.value.data);
      }
    } catch {
      setError('Failed to load feedback data');
    } finally {
      setLoading(false);
    }
  };

  const filteredFeedbacks = typeFilter === 'ALL'
    ? feedbacks
    : feedbacks.filter((f) => f.feedbackType === typeFilter);

  const averageRating = feedbacks.length > 0
    ? feedbacks.reduce((sum, f) => sum + f.rating, 0) / feedbacks.length
    : 0;

  const ratingDistribution = [5, 4, 3, 2, 1].map((rating) => ({
    rating: `${rating} Star${rating > 1 ? 's' : ''}`,
    count: feedbacks.filter((f) => f.rating === rating).length,
  }));

  const feedbackByType = ['STUDENT_FACULTY', 'STUDENT_COURSE', 'FACULTY_FEEDBACK', 'GENERAL'].map((type) => ({
    type: type.replace(/_/g, ' '),
    count: feedbacks.filter((f) => f.feedbackType === type).length,
    avgRating: feedbacks.filter((f) => f.feedbackType === type).length > 0
      ? feedbacks.filter((f) => f.feedbackType === type).reduce((s, f) => s + f.rating, 0) /
        feedbacks.filter((f) => f.feedbackType === type).length
      : 0,
  }));

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="Feedback Management" subtitle="View and analyze feedback" />

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h4" fontWeight={700} color="primary.main">{feedbacks.length}</Typography>
              <Typography variant="body2" color="text.secondary">Total Feedback</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h4" fontWeight={700} color="warning.main">{averageRating.toFixed(1)}</Typography>
              <Typography variant="body2" color="text.secondary">Average Rating</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h4" fontWeight={700} color="success.main">
                {feedbacks.filter((f) => f.rating >= 4).length}
              </Typography>
              <Typography variant="body2" color="text.secondary">Positive (4-5)</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h4" fontWeight={700} color="error.main">
                {feedbacks.filter((f) => f.rating <= 2).length}
              </Typography>
              <Typography variant="body2" color="text.secondary">Negative (1-2)</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Rating Distribution</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={ratingDistribution}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="rating" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="count" fill="#1565C0" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Feedback by Type</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={feedbackByType}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="type" tick={{ fontSize: 10 }} />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="count" fill="#42A5F5" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">All Feedback</Typography>
            <TextField
              select
              size="small"
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              sx={{ minWidth: 200 }}
            >
              <MenuItem value="ALL">All Types</MenuItem>
              <MenuItem value="STUDENT_FACULTY">Student - Faculty</MenuItem>
              <MenuItem value="STUDENT_COURSE">Student - Course</MenuItem>
              <MenuItem value="FACULTY_FEEDBACK">Faculty Feedback</MenuItem>
              <MenuItem value="GENERAL">General</MenuItem>
            </TextField>
          </Box>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>From</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Type</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Rating</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Comment</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Date</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredFeedbacks.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      <Typography color="text.secondary" sx={{ py: 2 }}>No feedback found</Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredFeedbacks.map((fb) => (
                    <TableRow key={fb.id} hover>
                      <TableCell>{fb.isAnonymous ? 'Anonymous' : fb.fromUserName}</TableCell>
                      <TableCell><Chip label={fb.feedbackType.replace(/_/g, ' ')} size="small" variant="outlined" /></TableCell>
                      <TableCell><Rating value={fb.rating} readOnly size="small" /></TableCell>
                      <TableCell>{fb.comment}</TableCell>
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

export default FeedbackPage;
