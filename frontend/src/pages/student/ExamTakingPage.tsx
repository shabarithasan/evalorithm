import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Radio,
  RadioGroup,
  FormControlLabel,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Snackbar,
  Alert,
  CircularProgress,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import FlagIcon from '@mui/icons-material/Flag';
import ClearIcon from '@mui/icons-material/Clear';
import SaveIcon from '@mui/icons-material/Save';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ExamHeader from '../../components/exam/ExamHeader';
import QuestionPalette from '../../components/exam/QuestionPalette';
import Webcam from 'react-webcam';
import * as cocoSsd from '@tensorflow-models/coco-ssd';
import '@tensorflow/tfjs-backend-cpu';
import '@tensorflow/tfjs-backend-webgl';
import '@tensorflow/tfjs';
import { examTakingService } from '../../services';
import { LiveExamQuestion, StudentAnswer, AnswerStatusType } from '../../types';

const ExamTakingPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [attemptId, setAttemptId] = useState<number | null>(null);
  const [questions, setQuestions] = useState<LiveExamQuestion[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<number, StudentAnswer>>({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [examTitle, setExamTitle] = useState('');
  const [durationMinutes, setDurationMinutes] = useState(60);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [submitDialogOpen, setSubmitDialogOpen] = useState(false);
  const [warningDialog, setWarningDialog] = useState<{ open: boolean; message: string }>({ open: false, message: '' });

  const webcamRef = useRef<Webcam>(null);
  const [model, setModel] = useState<cocoSsd.ObjectDetection | null>(null);
  const [proctorMessage, setProctorMessage] = useState<string>('Initializing AI Proctor...');
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' | 'warning' }>({
    open: false, message: '', severity: 'success',
  });
  const [autoSaveStatus, setAutoSaveStatus] = useState<'idle' | 'saving' | 'saved'>('idle');
  const [tabSwitchCount, setTabSwitchCount] = useState(0);
  const [preventTabSwitch, setPreventTabSwitch] = useState(false);

  const autoSaveRef = useRef<NodeJS.Timeout | null>(null);
  const hasInitialized = useRef(false);

  useEffect(() => {
    if (!hasInitialized.current) {
      hasInitialized.current = true;
      initExam();
    }

    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (!isSubmitted && attemptId) {
        e.preventDefault();
        e.returnValue = 'You have an ongoing exam. Are you sure you want to leave?';
      }
    };

    const handleVisibilityChange = () => {
      if (document.hidden && preventTabSwitch && !isSubmitted) {
        const newCount = tabSwitchCount + 1;
        setTabSwitchCount(newCount);
        
        if (newCount >= 1) {
          // Immediately set force submit flag
          setSubmitDialogOpen(true);
          // And we will trigger the actual submit in another useEffect or via button
          setSnackbar({
            open: true,
            message: 'You switched tabs! The exam has been locked and will be submitted.',
            severity: 'error',
          });
          // Hack: just click the submit button in the DOM if it exists, or dispatch a custom event
          window.dispatchEvent(new Event('force-submit-exam'));
        }
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    document.addEventListener('visibilitychange', handleVisibilityChange);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      if (autoSaveRef.current) clearInterval(autoSaveRef.current);
    };
  }, [isSubmitted, attemptId, preventTabSwitch, tabSwitchCount]);

  useEffect(() => {
    if (preventTabSwitch && !isSubmitted) {
      cocoSsd.load().then((loadedModel: cocoSsd.ObjectDetection) => {
        setModel(loadedModel);
        setProctorMessage('AI Proctor Active (Monitoring...)');
      }).catch((err: any) => {
        console.error('Failed to load AI Proctor model', err);
        setProctorMessage('AI Proctor Error');
      });
    }
  }, [preventTabSwitch, isSubmitted]);

  useEffect(() => {
    if (!model || !preventTabSwitch || isSubmitted) return;

    const interval = setInterval(async () => {
      if (webcamRef.current && webcamRef.current.video && webcamRef.current.video.readyState === 4) {
        const video = webcamRef.current.video;
        // Ultra-aggressive: 0.2 score to catch ANY part of a phone or screen
        const predictions = await model.detect(video, 20, 0.2);
        
        let hasPerson = false;
        let hasPhone = false;

        const forbiddenItems = ['cell phone', 'laptop', 'tv', 'remote', 'tablet', 'book']; // included book just in case it looks like phone back

        predictions.forEach((prediction: any) => {
          if (prediction.class === 'person') hasPerson = true;
          if (forbiddenItems.includes(prediction.class)) hasPhone = true;
        });

        if (hasPhone) {
          setSubmitDialogOpen(true);
          setSnackbar({
            open: true,
            message: 'Device detected! The exam has been locked for cheating and will be submitted.',
            severity: 'error',
          });
          window.dispatchEvent(new Event('force-submit-exam'));
        } else if (!hasPerson) {
          const newCount = tabSwitchCount + 1; // Reuse the strike counter for out-of-frame
          setTabSwitchCount(newCount);
          if (newCount >= 5) {
            setSnackbar({
              open: true,
              message: 'Examinee left the camera radius too many times. Exam locked.',
              severity: 'error',
            });
            window.dispatchEvent(new Event('force-submit-exam'));
          } else {
            setSnackbar({
              open: true,
              message: `Warning: Please stay inside the camera radius! (${newCount}/5 warnings)`,
              severity: 'warning',
            });
          }
        }
      }
    }, 2500);

    return () => clearInterval(interval);
  }, [model, preventTabSwitch, isSubmitted]);

  const initExam = async () => {
    try {
      let res;
      const examStatus = await examTakingService.getExamStatus(Number(id));
      if (examStatus.success && examStatus.data?.attemptId) {
        res = await examTakingService.resumeExam(examStatus.data.attemptId);
        setAttemptId(examStatus.data.attemptId);
      } else {
        res = await examTakingService.startExam(Number(id));
        if (res.success) {
          setAttemptId(res.data.attemptId);
        }
      }

      if (res && res.success) {
        setExamTitle(res.data.title || res.data.examTitle || 'Exam');
        setDurationMinutes(res.data.durationMinutes || 60);
        setPreventTabSwitch(res.data.preventTabSwitch || false);

        const loadedQuestions = res.data.questions || [];
        if (loadedQuestions.length > 0) {
          setQuestions(loadedQuestions);

          const initialAnswers: Record<number, StudentAnswer> = {};
          loadedQuestions.forEach((q: any) => {
            if (q.userAnswer) {
              initialAnswers[q.examQuestionId] = q.userAnswer;
            } else {
              initialAnswers[q.examQuestionId] = {
                examQuestionId: q.examQuestionId,
                answerStatus: 'NOT_ANSWERED',
              };
            }
          });
          setAnswers(initialAnswers);
        }

        try { document.documentElement.requestFullscreen(); } catch {}

        autoSaveRef.current = setInterval(() => {
          if (attemptId && !isSubmitted) saveAllAnswers();
        }, 30000);
      }
    } catch (err: any) {
      setSnackbar({
        open: true,
        message: err.response?.data?.message || 'Failed to start exam',
        severity: 'error',
      });
      setTimeout(() => navigate('/student/exams'), 3000);
    } finally {
      setLoading(false);
    }
  };

  const saveAllAnswers = async () => {
    if (!attemptId) return;
    setAutoSaveStatus('saving');
    try {
      const currentQ = questions[currentIndex];
      if (currentQ) {
        const answer = answers[currentQ.examQuestionId];
        if (answer) {
          await examTakingService.saveAnswer(attemptId, answer);
        }
      }
      setAutoSaveStatus('saved');
      setTimeout(() => setAutoSaveStatus('idle'), 2000);
    } catch {
      setAutoSaveStatus('idle');
    }
  };

  const handleSelectOption = (examQuestionId: number, optionLabel: string) => {
    setAnswers((prev) => ({
      ...prev,
      [examQuestionId]: {
        ...prev[examQuestionId],
        examQuestionId,
        selectedOptionLabel: optionLabel,
        answerStatus: 'ANSWERED',
      },
    }));
  };

  const handleTextAnswer = (examQuestionId: number, text: string) => {
    setAnswers((prev) => ({
      ...prev,
      [examQuestionId]: {
        ...prev[examQuestionId],
        examQuestionId,
        textAnswer: text,
        answerStatus: text ? 'ANSWERED' : 'NOT_ANSWERED',
      },
    }));
  };

  const handleMarkForReview = () => {
    const currentQ = questions[currentIndex];
    if (!currentQ) return;
    const currentAnswer = answers[currentQ.examQuestionId];
    const isAnswered = currentAnswer?.selectedOptionLabel || currentAnswer?.textAnswer;

    setAnswers((prev) => ({
      ...prev,
      [currentQ.examQuestionId]: {
        ...prev[currentQ.examQuestionId],
        examQuestionId: currentQ.examQuestionId,
        answerStatus: isAnswered ? 'ANSWERED_MARKED' : 'MARKED_FOR_REVIEW',
      },
    }));
  };

  const handleClearAnswer = () => {
    const currentQ = questions[currentIndex];
    if (!currentQ) return;
    setAnswers((prev) => ({
      ...prev,
      [currentQ.examQuestionId]: {
        examQuestionId: currentQ.examQuestionId,
        answerStatus: 'NOT_ANSWERED',
      },
    }));
  };

  const handleSaveAndNext = async () => {
    if (!attemptId || !questions[currentIndex]) return;
    const currentQ = questions[currentIndex];
    const answer = answers[currentQ.examQuestionId];

    if (answer) {
      try {
        await examTakingService.saveAnswer(attemptId, answer);
      } catch {}
    }

    if (currentIndex < questions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    }
  };

  const handleJumpToQuestion = (index: number) => {
    setCurrentIndex(index);
  };

  const handleSubmitExam = async () => {
    if (!attemptId) return;
    setSubmitting(true);
    try {
      await saveAllAnswers();
      const res = await examTakingService.submitExam(attemptId);
      if (res.success) {
        setIsSubmitted(true);
        try { await document.exitFullscreen(); } catch {}
        setSnackbar({ open: true, message: 'Exam submitted successfully!', severity: 'success' });
        setTimeout(() => navigate('/student/exams'), 2000);
      }
    } catch (err: any) {
      setSnackbar({
        open: true,
        message: err.response?.data?.message || 'Failed to submit exam',
        severity: 'error',
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleTimeUp = () => {
    setWarningDialog({
      open: true,
      message: 'Time is up! The exam will be submitted automatically.',
    });
    setTimeout(() => handleSubmitExam(), 2000);
  };

  const handleWarning = (minutesLeft: number) => {
    setSnackbar({
      open: true,
      message: `Only ${minutesLeft} minute${minutesLeft > 1 ? 's' : ''} remaining!`,
      severity: 'warning',
    });
  };

  const getAnsweredCount = (): number => {
    return Object.values(answers).filter(
      (a) => a.answerStatus === 'ANSWERED' || a.answerStatus === 'ANSWERED_MARKED'
    ).length;
  };

  const handleKeyboardNav = useCallback((e: KeyboardEvent) => {
    if (isSubmitted) return;
    if (e.altKey && e.key === 'n') {
      e.preventDefault();
      if (currentIndex < questions.length - 1) setCurrentIndex((prev) => prev + 1);
    }
    if (e.altKey && e.key === 'p') {
      e.preventDefault();
      if (currentIndex > 0) setCurrentIndex((prev) => prev - 1);
    }
  }, [currentIndex, questions.length, isSubmitted]);

  useEffect(() => {
    window.addEventListener('keydown', handleKeyboardNav);
    return () => window.removeEventListener('keydown', handleKeyboardNav);
  }, [handleKeyboardNav]);

  useEffect(() => {
    const handleForceSubmit = () => {
      handleSubmitExam();
    };
    window.addEventListener('force-submit-exam', handleForceSubmit);
    return () => window.removeEventListener('force-submit-exam', handleForceSubmit);
  }, [handleSubmitExam]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', flexDirection: 'column', gap: 2 }}>
        <CircularProgress />
        <Typography>Preparing your exam...</Typography>
      </Box>
    );
  }

  if (isSubmitted) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', flexDirection: 'column', gap: 2 }}>
        <CheckCircleIcon sx={{ fontSize: 64, color: '#2E7D32' }} />
        <Typography variant="h4">Exam Submitted Successfully!</Typography>
        <Typography color="text.secondary">Redirecting to exams page...</Typography>
      </Box>
    );
  }

  const currentQ = questions[currentIndex];
  const currentAnswer = currentQ ? answers[currentQ.examQuestionId] : null;

  return (
    <Box sx={{ height: '100vh', display: 'flex', flexDirection: 'column', bgcolor: '#F5F7FA' }}>
      <ExamHeader
        examTitle={examTitle}
        durationMinutes={durationMinutes}
        currentQuestion={currentIndex + 1}
        totalQuestions={questions.length}
        onTimeUp={handleTimeUp}
        onWarning={handleWarning}
        onSubmit={() => setSubmitDialogOpen(true)}
      />

      <Box sx={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
        <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'auto', p: 3 }}>
          {currentQ && (
            <Box sx={{ bgcolor: '#fff', borderRadius: 2, p: 3, boxShadow: '0 1px 3px rgba(0,0,0,0.06)' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Box sx={{ bgcolor: 'primary.main', color: '#fff', px: 1.5, py: 0.5, borderRadius: 1, fontSize: '0.85rem', fontWeight: 600 }}>
                  Q{currentIndex + 1}
                </Box>
                <Typography variant="caption" color="text.secondary">
                  {currentQ.questionType.replace('_', ' ')} | {currentQ.marks} mark{currentQ.marks > 1 ? 's' : ''}
                </Typography>
                {currentAnswer?.answerStatus === 'MARKED_FOR_REVIEW' && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: '#9C27B0' }}>
                    <FlagIcon sx={{ fontSize: 16 }} />
                    <Typography variant="caption" sx={{ fontWeight: 600 }}>Marked for Review</Typography>
                  </Box>
                )}
              </Box>

              <Typography variant="body1" sx={{ mb: 0.5, fontWeight: 600, fontSize: '1.05rem' }}>
                {currentQ.questionTitle}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3, whiteSpace: 'pre-line' }}>
                {currentQ.questionDescription}
              </Typography>

              {currentQ.questionType === 'MCQ' && currentQ.options && (
                <RadioGroup
                  value={currentAnswer?.selectedOptionLabel || ''}
                  onChange={(e) => handleSelectOption(currentQ.examQuestionId, e.target.value)}
                >
                  {currentQ.options.map((opt) => (
                    <FormControlLabel
                      key={opt.optionLabel}
                      value={opt.optionLabel}
                      control={<Radio />}
                      label={
                        <Box>
                          <Typography variant="body2" sx={{ fontWeight: 500 }}>
                            {opt.optionLabel}. {opt.optionText}
                          </Typography>
                        </Box>
                      }
                      sx={{
                        mb: 1,
                        p: 1.5,
                        borderRadius: 1,
                        border: '1px solid',
                        borderColor: currentAnswer?.selectedOptionLabel === opt.optionLabel ? 'primary.main' : 'grey.200',
                        bgcolor: currentAnswer?.selectedOptionLabel === opt.optionLabel ? 'primary.50' : 'transparent',
                        '&:hover': { bgcolor: 'grey.50' },
                        transition: 'all 0.15s ease',
                      }}
                    />
                  ))}
                </RadioGroup>
              )}

              {currentQ.questionType === 'TRUE_FALSE' && currentQ.options && (
                <RadioGroup
                  value={currentAnswer?.selectedOptionLabel || ''}
                  onChange={(e) => handleSelectOption(currentQ.examQuestionId, e.target.value)}
                >
                  {currentQ.options.map((opt) => (
                    <FormControlLabel
                      key={opt.optionLabel}
                      value={opt.optionLabel}
                      control={<Radio />}
                      label={opt.optionText}
                      sx={{
                        mb: 1,
                        p: 1.5,
                        borderRadius: 1,
                        border: '1px solid',
                        borderColor: currentAnswer?.selectedOptionLabel === opt.optionLabel ? 'primary.main' : 'grey.200',
                        bgcolor: currentAnswer?.selectedOptionLabel === opt.optionLabel ? 'primary.50' : 'transparent',
                        '&:hover': { bgcolor: 'grey.50' },
                      }}
                    />
                  ))}
                </RadioGroup>
              )}

              {(currentQ.questionType === 'DESCRIPTIVE' || currentQ.questionType === 'PROGRAMMING' || currentQ.questionType === 'CASE_STUDY') && (
                <TextField
                  fullWidth
                  multiline
                  rows={6}
                  placeholder="Type your answer here..."
                  value={currentAnswer?.textAnswer || ''}
                  onChange={(e) => handleTextAnswer(currentQ.examQuestionId, e.target.value)}
                  variant="outlined"
                />
              )}

              {currentQ.questionType === 'FILL_BLANKS' && (
                <TextField
                  fullWidth
                  placeholder="Enter your answer..."
                  value={currentAnswer?.textAnswer || ''}
                  onChange={(e) => handleTextAnswer(currentQ.examQuestionId, e.target.value)}
                  variant="outlined"
                />
              )}
            </Box>
          )}

          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
            <Button
              variant="outlined"
              startIcon={<ArrowBackIcon />}
              disabled={currentIndex === 0}
              onClick={() => setCurrentIndex(currentIndex - 1)}
            >
              Previous
            </Button>

            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                color="warning"
                startIcon={<FlagIcon />}
                onClick={handleMarkForReview}
              >
                Mark for Review
              </Button>
              <Button
                variant="outlined"
                startIcon={<ClearIcon />}
                onClick={handleClearAnswer}
              >
                Clear
              </Button>
              {autoSaveStatus === 'saving' && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                  <CircularProgress size={16} />
                  <Typography variant="caption">Saving...</Typography>
                </Box>
              )}
              {autoSaveStatus === 'saved' && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: '#2E7D32' }}>
                  <CheckCircleIcon sx={{ fontSize: 16 }} />
                  <Typography variant="caption">Saved</Typography>
                </Box>
              )}
            </Box>

            <Button
              variant="contained"
              endIcon={<ArrowForwardIcon />}
              onClick={handleSaveAndNext}
              disabled={currentIndex === questions.length - 1}
            >
              Save & Next
            </Button>
          </Box>
        </Box>

        <Box sx={{ width: 280, p: 2, overflowY: 'auto', borderLeft: '1px solid', borderColor: 'grey.200', bgcolor: '#fff' }}>
          <QuestionPalette
            questions={questions}
            currentIndex={currentIndex}
            answers={answers}
            onJumpToQuestion={handleJumpToQuestion}
          />

          <Box sx={{ mt: 2, p: 1.5, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Typography variant="caption" color="text.secondary">
              Answered: {getAnsweredCount()} / {questions.length}
            </Typography>
          </Box>
        </Box>
      </Box>

      {preventTabSwitch && !isSubmitted && (
        <Box 
          sx={{ 
            position: 'fixed', 
            bottom: 24, 
            right: 24, 
            width: 240, 
            height: 180,
            zIndex: 1000, 
            boxShadow: '0 0 15px rgba(255, 0, 0, 0.6)', 
            borderRadius: 3, 
            overflow: 'hidden', 
            bgcolor: 'black', 
            border: '3px solid #ff1744' 
          }}
        >
          {/* REC Badge */}
          <Box sx={{
            position: 'absolute',
            top: 12,
            left: 12,
            bgcolor: '#ff1744',
            color: 'white',
            px: 1,
            py: 0.25,
            borderRadius: 1,
            typography: 'caption',
            fontWeight: 'bold',
            zIndex: 2,
            animation: 'pulse 1.5s infinite',
            '@keyframes pulse': {
              '0%': { opacity: 1 },
              '50%': { opacity: 0.5 },
              '100%': { opacity: 1 },
            }
          }}>
            REC
          </Box>
          
          {/* Webcam feed */}
          <Webcam 
            ref={webcamRef} 
            audio={false} 
            style={{ 
              width: '100%', 
              height: '100%', 
              objectFit: 'cover',
              transform: 'scaleX(-1)' // Mirror the webcam
            }} 
          />

          {/* Proctor Message Overlay */}
          <Box sx={{ 
            position: 'absolute', 
            bottom: 0, 
            left: 0, 
            right: 0, 
            p: 1, 
            background: 'linear-gradient(transparent, rgba(0,0,0,0.8))', 
            color: 'white', 
            textAlign: 'center', 
            typography: 'caption', 
            fontWeight: 'bold',
            zIndex: 2
          }}>
            {proctorMessage}
          </Box>
        </Box>
      )}

      <Dialog open={submitDialogOpen} onClose={() => setSubmitDialogOpen(false)}>
        <DialogTitle>Submit Exam?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            You have answered {getAnsweredCount()} out of {questions.length} questions.
            Are you sure you want to submit? You cannot change your answers after submission.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSubmitDialogOpen(false)} disabled={submitting}>Cancel</Button>
          <Button variant="contained" color="error" onClick={handleSubmitExam} disabled={submitting}>
            {submitting ? 'Submitting...' : 'Submit Exam'}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={warningDialog.open}>
        <DialogTitle>Time's Up!</DialogTitle>
        <DialogContent>
          <DialogContentText>{warningDialog.message}</DialogContentText>
        </DialogContent>
      </Dialog>

      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
        <Alert severity={snackbar.severity}>{snackbar.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default ExamTakingPage;
