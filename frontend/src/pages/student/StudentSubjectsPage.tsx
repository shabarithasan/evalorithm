import React, { useState, useEffect } from 'react';
import { Box, Grid, Card, CardContent, Typography, Chip } from '@mui/material';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import EmptyState from '../../components/common/EmptyState';
import { profileService, subjectService } from '../../services';
import { Subject } from '../../types';

const StudentSubjectsPage: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const userRes = await profileService.getCurrentUser();
      if (userRes.success) {
        const response = await subjectService.getAll(0, 100);
        if (response.success) {
          setSubjects(response.data.content);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load subjects');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <PageHeader title="My Subjects" subtitle="Subjects you are enrolled in" />

      {subjects.length === 0 ? (
        <EmptyState title="No subjects enrolled" message="You are not currently enrolled in any subjects." />
      ) : (
        <Grid container spacing={3}>
          {subjects.map((subject) => (
            <Grid item xs={12} sm={6} md={4} key={subject.id}>
              <Card sx={{ height: '100%', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-2px)' } }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                    <Box
                      sx={{
                        p: 1,
                        borderRadius: 1.5,
                        bgcolor: 'primary.50',
                        display: 'flex',
                        alignItems: 'center',
                      }}
                    >
                      <MenuBookIcon sx={{ color: 'primary.main' }} />
                    </Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                      {subject.code}
                    </Typography>
                  </Box>
                  <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem' }}>
                    {subject.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {subject.description || 'No description available'}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                    <Chip
                      label={`${subject.credits} Credits`}
                      size="small"
                      variant="outlined"
                    />
                    <Chip
                      label={subject.departmentName}
                      size="small"
                      variant="outlined"
                      color="primary"
                    />
                    <Chip
                      label={`Sem ${subject.semesterNumber}`}
                      size="small"
                      variant="outlined"
                    />
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default StudentSubjectsPage;
