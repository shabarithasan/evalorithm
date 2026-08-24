import React from 'react';
import {
  Box,
  TextField,
  Typography,
  IconButton,
  Paper,
  Button,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { CaseStudyData } from '../../types';

interface CaseStudyEditorProps {
  data: CaseStudyData;
  onChange: (data: CaseStudyData) => void;
}

const CaseStudyEditor: React.FC<CaseStudyEditorProps> = ({ data, onChange }) => {
  const subQuestions: string[] = data.subQuestions ? JSON.parse(data.subQuestions || '[]') : [];

  const updateSubQuestions = (items: string[]) => {
    onChange({ ...data, subQuestions: JSON.stringify(items) });
  };

  const addSubQuestion = () => {
    updateSubQuestions([...subQuestions, '']);
  };

  const removeSubQuestion = (index: number) => {
    updateSubQuestions(subQuestions.filter((_: string, i: number) => i !== index));
  };

  const updateSubQuestion = (index: number, value: string) => {
    const updated = [...subQuestions];
    updated[index] = value;
    updateSubQuestions(updated);
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Typography variant="subtitle2">Case Study</Typography>

      <TextField
        label="Scenario / Case Description"
        value={data.scenario}
        onChange={(e) => onChange({ ...data, scenario: e.target.value })}
        multiline
        rows={6}
        fullWidth
        size="small"
        placeholder="Describe the case study scenario in detail..."
      />

      <Box>
        <Typography variant="subtitle2" sx={{ mb: 1 }}>Sub-Questions</Typography>
        {subQuestions.map((sq: string, i: number) => (
          <Paper key={i} variant="outlined" sx={{ p: 1.5, mb: 1.5, display: 'flex', gap: 1, alignItems: 'flex-start' }}>
            <Typography variant="body2" sx={{ fontWeight: 600, mt: 1, minWidth: 20 }}>
              Q{i + 1}.
            </Typography>
            <TextField
              size="small"
              value={sq}
              onChange={(e) => updateSubQuestion(i, e.target.value)}
              multiline
              rows={2}
              fullWidth
              placeholder={`Sub-question ${i + 1}`}
            />
            <IconButton size="small" color="error" onClick={() => removeSubQuestion(i)}>
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Paper>
        ))}
        <Button startIcon={<AddIcon />} onClick={addSubQuestion} size="small">
          Add Sub-Question
        </Button>
      </Box>
    </Box>
  );
};

export default CaseStudyEditor;
