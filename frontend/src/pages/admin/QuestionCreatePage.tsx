import React from 'react';
import { Box, Typography, Breadcrumbs, Link } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import QuestionForm from '../../components/questions/QuestionForm';
import { questionService } from '../../services';
import { QuestionRequest } from '../../types';

const QuestionCreatePage: React.FC = () => {
  const navigate = useNavigate();

  const handleSave = async (data: QuestionRequest) => {
    await questionService.create(data);
    navigate('/admin/questions');
  };

  return (
    <Box>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/admin/questions" underline="hover" color="inherit">
          Questions
        </Link>
        <Typography color="text.primary">Create Question</Typography>
      </Breadcrumbs>

      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>
        Create New Question
      </Typography>

      <QuestionForm onSave={handleSave} />
    </Box>
  );
};

export default QuestionCreatePage;
