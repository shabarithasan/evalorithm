import React, { useState } from 'react';
import {
  Box,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
} from '@mui/material';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import FullscreenExitIcon from '@mui/icons-material/FullscreenExit';
import ExamTimer from './ExamTimer';

interface ExamHeaderProps {
  examTitle: string;
  durationMinutes: number;
  currentQuestion: number;
  totalQuestions: number;
  onTimeUp: () => void;
  onWarning?: (minutesLeft: number) => void;
  onSubmit: () => void;
  isPaused?: boolean;
}

const ExamHeader: React.FC<ExamHeaderProps> = ({
  examTitle,
  durationMinutes,
  currentQuestion,
  totalQuestions,
  onTimeUp,
  onWarning,
  onSubmit,
  isPaused,
}) => {
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [submitDialogOpen, setSubmitDialogOpen] = useState(false);

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen().then(() => setIsFullscreen(true));
    } else {
      document.exitFullscreen().then(() => setIsFullscreen(false));
    }
  };

  return (
    <>
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          px: 3,
          py: 1.5,
          bgcolor: '#fff',
          borderBottom: '1px solid',
          borderColor: 'grey.200',
          boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 600, color: 'primary.main' }}>
            {examTitle}
          </Typography>
          <Box sx={{ bgcolor: 'grey.100', px: 1.5, py: 0.5, borderRadius: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Question {currentQuestion} of {totalQuestions}
            </Typography>
          </Box>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <ExamTimer
            totalMinutes={durationMinutes}
            onTimeUp={onTimeUp}
            onWarning={onWarning}
            isPaused={isPaused}
          />

          <Button
            variant="outlined"
            size="small"
            onClick={toggleFullscreen}
            startIcon={isFullscreen ? <FullscreenExitIcon /> : <FullscreenIcon />}
          >
            {isFullscreen ? 'Exit' : 'Fullscreen'}
          </Button>

          <Button
            variant="contained"
            color="error"
            onClick={() => setSubmitDialogOpen(true)}
          >
            Submit Exam
          </Button>
        </Box>
      </Box>

      <Dialog open={submitDialogOpen} onClose={() => setSubmitDialogOpen(false)}>
        <DialogTitle>Submit Exam?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to submit the exam? You won't be able to change your answers after submission.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSubmitDialogOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => {
              setSubmitDialogOpen(false);
              onSubmit();
            }}
          >
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default ExamHeader;
