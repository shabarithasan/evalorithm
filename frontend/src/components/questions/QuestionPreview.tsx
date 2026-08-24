import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Chip,
  Divider,
  Grid,
  Paper,
  Radio,
  RadioGroup,
  FormControlLabel,
} from '@mui/material';
import QuestionTypeBadge from './QuestionTypeBadge';
import DifficultyBadge from './DifficultyBadge';
import BloomLevelBadge from './BloomLevelBadge';
import { Question } from '../../types';

interface QuestionPreviewProps {
  question: Question;
}

const QuestionPreview: React.FC<QuestionPreviewProps> = ({ question }) => {
  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
          <QuestionTypeBadge type={question.questionType} />
          <DifficultyBadge difficulty={question.difficulty} />
          <BloomLevelBadge level={question.bloomLevel} />
          <Chip size="small" label={`${question.marks} marks`} variant="outlined" />
          <Chip size="small" label={`${question.estimatedTime} min`} variant="outlined" />
        </Box>

        <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>{question.title}</Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 2, whiteSpace: 'pre-wrap' }}>
          {question.description}
        </Typography>

        <Divider sx={{ my: 2 }} />

        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid item xs={6} sm={3}>
            <Typography variant="caption" color="text.secondary">Department</Typography>
            <Typography variant="body2" fontWeight={500}>{question.departmentName}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="caption" color="text.secondary">Semester</Typography>
            <Typography variant="body2" fontWeight={500}>{question.semesterNumber}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="caption" color="text.secondary">Subject</Typography>
            <Typography variant="body2" fontWeight={500}>{question.subjectName}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="caption" color="text.secondary">Unit</Typography>
            <Typography variant="body2" fontWeight={500}>{question.unitName}</Typography>
          </Grid>
          {question.topicName && (
            <Grid item xs={6} sm={3}>
              <Typography variant="caption" color="text.secondary">Topic</Typography>
              <Typography variant="body2" fontWeight={500}>{question.topicName}</Typography>
            </Grid>
          )}
          <Grid item xs={6} sm={3}>
            <Typography variant="caption" color="text.secondary">Category</Typography>
            <Typography variant="body2" fontWeight={500}>{question.categoryName}</Typography>
          </Grid>
        </Grid>

        <Divider sx={{ my: 2 }} />

        {/* MCQ Options */}
        {question.questionType === 'MCQ' && question.mcqOptions && (
          <Box sx={{ mb: 2 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>Options</Typography>
            <RadioGroup value={question.mcqOptions.findIndex((o) => o.isCorrect).toString()}>
              {question.mcqOptions.map((opt, i) => (
                <Paper
                  key={i}
                  variant="outlined"
                  sx={{
                    p: 1.5,
                    mb: 1,
                    borderColor: opt.isCorrect ? 'success.main' : 'grey.300',
                    backgroundColor: opt.isCorrect ? 'success.50' : 'transparent',
                  }}
                >
                  <FormControlLabel
                    value={i.toString()}
                    control={<Radio size="small" disabled />}
                    label={
                      <Box>
                        <Typography variant="body2" fontWeight={500}>
                          <strong>{opt.optionLabel}.</strong> {opt.optionText}
                        </Typography>
                        {opt.explanation && (
                          <Typography variant="caption" color="text.secondary">
                            Explanation: {opt.explanation}
                          </Typography>
                        )}
                      </Box>
                    }
                  />
                </Paper>
              ))}
            </RadioGroup>
          </Box>
        )}

        {/* Programming */}
        {question.questionType === 'PROGRAMMING' && question.programmingQuestion && (
          <Box sx={{ mb: 2 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>Programming Problem</Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
              <Paper variant="outlined" sx={{ p: 2 }}>
                <Typography variant="caption" color="text.secondary">Language</Typography>
                <Typography variant="body2" fontWeight={600}>{question.programmingQuestion.programmingLanguage}</Typography>
              </Paper>
              <Paper variant="outlined" sx={{ p: 2 }}>
                <Typography variant="caption" color="text.secondary">Problem Statement</Typography>
                <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.programmingQuestion.problemStatement}</Typography>
              </Paper>
              <Grid container spacing={1.5}>
                <Grid item xs={6}>
                  <Paper variant="outlined" sx={{ p: 2 }}>
                    <Typography variant="caption" color="text.secondary">Input Format</Typography>
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.programmingQuestion.inputFormat}</Typography>
                  </Paper>
                </Grid>
                <Grid item xs={6}>
                  <Paper variant="outlined" sx={{ p: 2 }}>
                    <Typography variant="caption" color="text.secondary">Output Format</Typography>
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.programmingQuestion.outputFormat}</Typography>
                  </Paper>
                </Grid>
              </Grid>
              {question.programmingQuestion.constraints && (
                <Paper variant="outlined" sx={{ p: 2 }}>
                  <Typography variant="caption" color="text.secondary">Constraints</Typography>
                  <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.programmingQuestion.constraints}</Typography>
                </Paper>
              )}
              <Grid container spacing={1.5}>
                <Grid item xs={6}>
                  <Paper variant="outlined" sx={{ p: 2 }}>
                    <Typography variant="caption" color="text.secondary">Sample Input</Typography>
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace' }}>{question.programmingQuestion.sampleInput}</Typography>
                  </Paper>
                </Grid>
                <Grid item xs={6}>
                  <Paper variant="outlined" sx={{ p: 2 }}>
                    <Typography variant="caption" color="text.secondary">Sample Output</Typography>
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace' }}>{question.programmingQuestion.sampleOutput}</Typography>
                  </Paper>
                </Grid>
              </Grid>
            </Box>
          </Box>
        )}

        {/* Case Study */}
        {question.questionType === 'CASE_STUDY' && question.caseStudy && (
          <Box sx={{ mb: 2 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>Case Study</Typography>
            <Paper variant="outlined" sx={{ p: 2, mb: 1.5 }}>
              <Typography variant="caption" color="text.secondary">Scenario</Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.caseStudy.scenario}</Typography>
            </Paper>
            {question.caseStudy.subQuestions && (() => {
              const subQs: string[] = JSON.parse(question.caseStudy.subQuestions || '[]');
              return subQs.map((sq: string, i: number) => (
                <Paper key={i} variant="outlined" sx={{ p: 1.5, mb: 1 }}>
                  <Typography variant="body2">
                    <strong>Q{i + 1}.</strong> {sq}
                  </Typography>
                </Paper>
              ));
            })()}
          </Box>
        )}

        {question.explanation && (
          <Box sx={{ mb: 2 }}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>Explanation</Typography>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>{question.explanation}</Typography>
          </Box>
        )}

        {question.reference && (
          <Box sx={{ mb: 1 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>Reference</Typography>
            <Typography variant="body2" color="text.secondary">{question.reference}</Typography>
          </Box>
        )}

        <Divider sx={{ my: 2 }} />
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          {question.courseOutcome && (
            <Box>
              <Typography variant="caption" color="text.secondary">Course Outcome</Typography>
              <Typography variant="body2">{question.courseOutcome}</Typography>
            </Box>
          )}
          {question.programOutcome && (
            <Box>
              <Typography variant="caption" color="text.secondary">Program Outcome</Typography>
              <Typography variant="body2">{question.programOutcome}</Typography>
            </Box>
          )}
          {question.programSpecificOutcome && (
            <Box>
              <Typography variant="caption" color="text.secondary">Program Specific Outcome</Typography>
              <Typography variant="body2">{question.programSpecificOutcome}</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default QuestionPreview;
