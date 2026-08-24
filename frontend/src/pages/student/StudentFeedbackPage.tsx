import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Rating,
  Grid,
  Alert,
  List,
  ListItem,
  ListItemText,
  Divider,
  Chip,
  Tab,
  Tabs,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { feedbackService, subjectService } from '../../services';
import { Feedback, Subject } from '../../types';

const StudentFeedbackPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [myFeedback, setMyFeedback] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  // Faculty feedback form
  const [facultyRating, setFacultyRating] = useState<number | null>(0);
  const [facultyComment, setFacultyComment] = useState('');
  const [facultySuggestions, setFacultySuggestions] = useState('');
  const [isAnonymous, setIsAnonymous] = useState(false);

  // Course feedback form
  const [selectedSubject, setSelectedSubject] = useState<number>(0);
  const [courseRating, setCourseRating] = useState<number | null>(0);
  const [courseComment, setCourseComment] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [subRes, fbRes] = await Promise.allSettled([
        subjectService.getAll(),
        feedbackService.getMyFeedback(),
      ]);
      if (subRes.status === 'fulfilled' && subRes.value.success) {
        setSubjects(subRes.value.data?.content || subRes.value.data || []);
      }
      if (fbRes.status === 'fulfilled' && fbRes.value.success) {
        setMyFeedback(fbRes.value.data?.content || fbRes.value.data || []);
      }
    } catch {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitFacultyFeedback = async () => {
    if (!facultyRating || !facultyComment) {
      setError('Please provide rating and comment');
      return;
    }
    try {
      await feedbackService.create({
        feedbackType: 'STUDENT_FACULTY',
        rating: facultyRating,
        comment: facultyComment,
        suggestions: facultySuggestions,
        isAnonymous,
      });
      setSuccess('Faculty feedback submitted');
      setFacultyRating(0);
      setFacultyComment('');
      setFacultySuggestions('');
      fetchData();
    } catch {
      setError('Failed to submit feedback');
    }
  };

  const handleSubmitCourseFeedback = async () => {
    if (!courseRating || !courseComment || !selectedSubject) {
      setError('Please fill all fields');
      return;
    }
    try {
      await feedbackService.create({
        feedbackType: 'STUDENT_COURSE',
        rating: courseRating,
        comment: courseComment,
        subjectId: selectedSubject,
        isAnonymous: false,
      });
      setSuccess('Course feedback submitted');
      setCourseRating(0);
      setCourseComment('');
      setSelectedSubject(0);
      fetchData();
    } catch {
      setError('Failed to submit feedback');
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="Feedback" subtitle="Share your feedback on faculty and courses" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
            <Tab label="Rate Faculty" />
            <Tab label="Rate Course" />
            <Tab label="My Feedback History" />
          </Tabs>

          {tabValue === 0 && (
            <Box sx={{ pt: 3, maxWidth: 500 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>Rate Faculty Performance</Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" sx={{ mb: 0.5 }}>Rating</Typography>
                <Rating
                  value={facultyRating}
                  onChange={(_, newValue) => setFacultyRating(newValue)}
                  size="large"
                />
              </Box>
              <TextField
                label="Comment"
                value={facultyComment}
                onChange={(e) => setFacultyComment(e.target.value)}
                fullWidth
                multiline
                rows={3}
                sx={{ mb: 2 }}
              />
              <TextField
                label="Suggestions"
                value={facultySuggestions}
                onChange={(e) => setFacultySuggestions(e.target.value)}
                fullWidth
                multiline
                rows={2}
                sx={{ mb: 2 }}
              />
              <Button variant="contained" startIcon={<SendIcon />} onClick={handleSubmitFacultyFeedback}>
                Submit Feedback
              </Button>
            </Box>
          )}

          {tabValue === 1 && (
            <Box sx={{ pt: 3, maxWidth: 500 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>Rate Course</Typography>
              <TextField
                select
                label="Subject"
                value={selectedSubject || ''}
                onChange={(e) => setSelectedSubject(Number(e.target.value))}
                fullWidth
                sx={{ mb: 2 }}
              >
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </TextField>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" sx={{ mb: 0.5 }}>Rating</Typography>
                <Rating
                  value={courseRating}
                  onChange={(_, newValue) => setCourseRating(newValue)}
                  size="large"
                />
              </Box>
              <TextField
                label="Comment"
                value={courseComment}
                onChange={(e) => setCourseComment(e.target.value)}
                fullWidth
                multiline
                rows={3}
                sx={{ mb: 2 }}
              />
              <Button variant="contained" startIcon={<SendIcon />} onClick={handleSubmitCourseFeedback}>
                Submit Feedback
              </Button>
            </Box>
          )}

          {tabValue === 2 && (
            <Box sx={{ pt: 2 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>My Feedback History</Typography>
              {myFeedback.length === 0 ? (
                <Typography color="text.secondary">No feedback submitted yet.</Typography>
              ) : (
                <List>
                  {myFeedback.map((fb, index) => (
                    <React.Fragment key={fb.id}>
                      <ListItem alignItems="flex-start">
                        <ListItemText
                          primary={
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                              <Chip label={fb.feedbackType.replace(/_/g, ' ')} size="small" color="primary" variant="outlined" />
                              <Rating value={fb.rating} readOnly size="small" />
                            </Box>
                          }
                          secondary={
                            <>
                              <Typography variant="body2" sx={{ mt: 0.5 }}>{fb.comment}</Typography>
                              <Typography variant="caption" color="text.secondary">
                                {new Date(fb.createdAt).toLocaleDateString()}
                                {fb.subjectName && ` | ${fb.subjectName}`}
                              </Typography>
                            </>
                          }
                        />
                      </ListItem>
                      {index < myFeedback.length - 1 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              )}
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default StudentFeedbackPage;
