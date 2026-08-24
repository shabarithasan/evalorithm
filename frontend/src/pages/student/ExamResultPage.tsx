import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Grid,
  CircularProgress,
  IconButton,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import DownloadIcon from '@mui/icons-material/Download';
import ResultCard from '../../components/exam/ResultCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { examResultService, examService } from '../../services';
import { ExamResult } from '../../types';

const ExamResultPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [result, setResult] = useState<ExamResult | null>(null);
  const [details, setDetails] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) fetchResult();
  }, [id]);

  const fetchResult = async () => {
    try {
      const resultRes = await examResultService.getResult(Number(id), 0);
      if (resultRes.success) {
        setResult(resultRes.data);
        const detailsRes = await examResultService.getResultDetails(resultRes.data.id);
        if (detailsRes.success) setDetails(detailsRes.data);
      }
    } catch {
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (!result) return <Typography sx={{ p: 3 }}>No result found</Typography>;

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
        <IconButton onClick={() => navigate('/student/exams')}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h4" sx={{ fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Exam Result</Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={5}>
          <ResultCard
            totalObtained={result.totalMarksObtained}
            totalPossible={result.totalMarksPossible}
            percentage={result.percentage}
            grade={result.grade}
            isPassed={result.isPassed}
            correctAnswers={result.correctAnswers}
            wrongAnswers={result.wrongAnswers}
            skippedQuestions={result.skippedQuestions}
            timeTakenMinutes={result.timeTakenMinutes}
          />
        </Grid>

        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Question-by-Question Review</Typography>
              {details?.questionDetails?.map((q: any, idx: number) => (
                <Box
                  key={idx}
                  sx={{
                    p: 2,
                    mb: 1.5,
                    borderRadius: 1,
                    border: '1px solid',
                    borderColor: q.isCorrect ? '#2E7D3230' : '#C6282830',
                    bgcolor: q.isCorrect ? '#2E7D3208' : '#C6282808',
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      Q{idx + 1}. {q.questionTitle}
                    </Typography>
                    <Typography
                      variant="caption"
                      sx={{
                        color: q.isCorrect ? '#2E7D32' : '#C62828',
                        fontWeight: 600,
                      }}
                    >
                      {q.isCorrect ? 'Correct' : 'Wrong'}
                    </Typography>
                  </Box>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                    Your Answer: <strong>{q.userAnswer || 'Not answered'}</strong>
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Correct Answer: <strong>{q.correctAnswer}</strong>
                  </Typography>
                  {q.explanation && (
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1, fontStyle: 'italic' }}>
                      {q.explanation}
                    </Typography>
                  )}
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ExamResultPage;
