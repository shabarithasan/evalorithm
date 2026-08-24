import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Breadcrumbs, Link, Paper, List, ListItem, ListItemText } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import QuestionForm from '../../components/questions/QuestionForm';
import LoadingScreen from '../../components/common/LoadingScreen';
import { questionService } from '../../services';
import { Question, QuestionRequest, QuestionVersion } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const QuestionEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question | null>(null);
  const [versions, setVersions] = useState<QuestionVersion[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    if (!id) return;
    try {
      const [qRes, vRes] = await Promise.all([
        questionService.getById(Number(id)),
        questionService.getVersions(Number(id)),
      ]);
      if (qRes.success) setQuestion(qRes.data);
      if (vRes.success) setVersions(vRes.data);
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (data: QuestionRequest) => {
    if (!id) return;
    await questionService.update(Number(id), data);
    navigate(`/admin/questions/${id}`);
  };

  if (loading) return <LoadingScreen />;
  if (!question) return <Typography color="error" sx={{ p: 3 }}>Question not found</Typography>;

  return (
    <Box>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/admin/questions" underline="hover" color="inherit">
          Questions
        </Link>
        <Link component={RouterLink} to={`/admin/questions/${id}`} underline="hover" color="inherit">
          {question.title}
        </Link>
        <Typography color="text.primary">Edit</Typography>
      </Breadcrumbs>

      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>
        Edit Question
      </Typography>

      <Box sx={{ display: 'flex', gap: 3 }}>
        <Box sx={{ flex: 1 }}>
          <QuestionForm existingQuestion={question} onSave={handleSave} />
        </Box>

        {versions.length > 0 && (
          <Paper sx={{ width: 280, p: 2, alignSelf: 'flex-start', position: 'sticky', top: 88 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>
              Version History
            </Typography>
            <List dense>
              {versions.map((v) => (
                <ListItem key={v.id} sx={{ px: 0 }}>
                  <ListItemText
                    primary={`v${v.versionNumber}`}
                    secondary={
                      <>
                        <Typography variant="caption" display="block">{v.updatedByName}</Typography>
                        <Typography variant="caption" color="text.secondary">{formatDateTime(v.createdAt)}</Typography>
                        {v.changeDescription && (
                          <Typography variant="caption" display="block" color="text.secondary">{v.changeDescription}</Typography>
                        )}
                      </>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </Paper>
        )}
      </Box>
    </Box>
  );
};

export default QuestionEditPage;
