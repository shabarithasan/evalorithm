import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Breadcrumbs,
  Link,
  Button,
  Grid,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemText,
  Alert,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import EditIcon from '@mui/icons-material/Edit';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import ArchiveIcon from '@mui/icons-material/Archive';
import DeleteIcon from '@mui/icons-material/Delete';
import SendIcon from '@mui/icons-material/Send';
import LoadingScreen from '../../components/common/LoadingScreen';
import QuestionPreview from '../../components/questions/QuestionPreview';
import QuestionStatsCard from '../../components/questions/QuestionStatsCard';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { questionService } from '../../services';
import { Question, QuestionVersion, QuestionApproval } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const QuestionDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question | null>(null);
  const [versions, setVersions] = useState<QuestionVersion[]>([]);
  const [loading, setLoading] = useState(true);
  const [confirmDialog, setConfirmDialog] = useState<{ open: boolean; title: string; message: string; onConfirm: () => void }>({
    open: false, title: '', message: '', onConfirm: () => {},
  });

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

  const handleDuplicate = async () => {
    if (!id) return;
    try {
      const res = await questionService.duplicate(Number(id));
      if (res.success) navigate(`/admin/questions/${res.data.id}/edit`);
    } catch {}
  };

  const handleArchive = async () => {
    if (!id) return;
    try {
      await questionService.archive(Number(id));
      fetchData();
    } catch {}
  };

  const handleDelete = () => {
    setConfirmDialog({
      open: true,
      title: 'Delete Question',
      message: 'Are you sure you want to delete this question? This action cannot be undone.',
      onConfirm: async () => {
        if (!id) return;
        try {
          await questionService.delete(Number(id));
          navigate('/admin/questions');
        } catch {}
      },
    });
  };

  const handleSubmitForReview = async () => {
    if (!id) return;
    try {
      await questionService.submitForReview(Number(id));
      fetchData();
    } catch {}
  };

  if (loading) return <LoadingScreen />;
  if (!question) return <Typography color="error" sx={{ p: 3 }}>Question not found</Typography>;

  return (
    <Box>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/admin/questions" underline="hover" color="inherit">
          Questions
        </Link>
        <Typography color="text.primary">{question.title}</Typography>
      </Breadcrumbs>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontSize: { xs: '1.3rem', sm: '1.6rem' } }}>
          {question.title}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Button variant="outlined" startIcon={<EditIcon />} onClick={() => navigate(`/admin/questions/${id}/edit`)}>
            Edit
          </Button>
          <Button variant="outlined" startIcon={<ContentCopyIcon />} onClick={handleDuplicate}>
            Duplicate
          </Button>
          {question.status === 'DRAFT' && (
            <Button variant="contained" startIcon={<SendIcon />} onClick={handleSubmitForReview}>
              Submit for Review
            </Button>
          )}
          <Button variant="outlined" startIcon={<ArchiveIcon />} onClick={handleArchive}>
            {question.isArchived ? 'Restore' : 'Archive'}
          </Button>
          <Button variant="outlined" color="error" startIcon={<DeleteIcon />} onClick={handleDelete}>
            Delete
          </Button>
        </Box>
      </Box>

      {question.status === 'PENDING_REVIEW' && (
        <Alert severity="warning" sx={{ mb: 2 }}>This question is pending review approval.</Alert>
      )}
      {question.status === 'APPROVED' && (
        <Alert severity="success" sx={{ mb: 2 }}>This question has been approved.</Alert>
      )}
      {question.status === 'REJECTED' && (
        <Alert severity="error" sx={{ mb: 2 }}>This question has been rejected.</Alert>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} lg={8}>
          <QuestionPreview question={question} />
        </Grid>
        <Grid item xs={12} lg={4}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {question.statistics && (
              <QuestionStatsCard statistics={question.statistics} />
            )}

            {versions.length > 0 && (
              <Paper sx={{ p: 2 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>Version History</Typography>
                <List dense>
                  {versions.map((v) => (
                    <ListItem key={v.id} sx={{ px: 0 }}>
                      <ListItemText
                        primary={`v${v.versionNumber}`}
                        secondary={
                          <>
                            <Typography variant="caption" display="block">{v.updatedByName}</Typography>
                            <Typography variant="caption" color="text.secondary">{formatDateTime(v.createdAt)}</Typography>
                          </>
                        }
                      />
                    </ListItem>
                  ))}
                </List>
              </Paper>
            )}
          </Box>
        </Grid>
      </Grid>

      <ConfirmDialog
        open={confirmDialog.open}
        onClose={() => setConfirmDialog((prev) => ({ ...prev, open: false }))}
        onConfirm={confirmDialog.onConfirm}
        title={confirmDialog.title}
        message={confirmDialog.message}
      />
    </Box>
  );
};

export default QuestionDetailPage;
