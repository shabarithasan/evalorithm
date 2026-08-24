import React from 'react';
import {
  Box,
  TextField,
  IconButton,
  Typography,
  Radio,
  RadioGroup,
  FormControlLabel,
  Paper,
  Button,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { MCQOption } from '../../types';

interface MCQOptionsEditorProps {
  options: MCQOption[];
  onChange: (options: MCQOption[]) => void;
}

const defaultLabels = ['A', 'B', 'C', 'D', 'E', 'F'];

const MCQOptionsEditor: React.FC<MCQOptionsEditorProps> = ({ options, onChange }) => {
  const addOption = () => {
    if (options.length >= 6) return;
    const label = defaultLabels[options.length] || String.fromCharCode(65 + options.length);
    onChange([...options, { optionLabel: label, optionText: '', isCorrect: false, explanation: '' }]);
  };

  const removeOption = (index: number) => {
    if (options.length <= 4) return;
    const updated = options.filter((_, i) => i !== index);
    updated.forEach((opt, i) => { opt.optionLabel = defaultLabels[i]; });
    onChange(updated);
  };

  const updateOption = (index: number, field: keyof MCQOption, value: any) => {
    const updated = [...options];
    updated[index] = { ...updated[index], [field]: value };
    onChange(updated);
  };

  const setCorrect = (index: number) => {
    const updated = options.map((opt, i) => ({ ...opt, isCorrect: i === index }));
    onChange(updated);
  };

  return (
    <Box>
      <Typography variant="subtitle2" sx={{ mb: 1.5 }}>
        Options (Select the correct answer)
      </Typography>
      <RadioGroup value={options.findIndex((o) => o.isCorrect).toString()}>
        {options.map((option, index) => (
          <Paper key={index} variant="outlined" sx={{ p: 2, mb: 1.5, display: 'flex', alignItems: 'flex-start', gap: 1.5 }}>
            <FormControlLabel
              value={index.toString()}
              control={<Radio size="small" />}
              label=""
              sx={{ mt: 0.5, ml: 0 }}
              onChange={() => setCorrect(index)}
            />
            <Box sx={{ flex: 1 }}>
              <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                <Typography variant="subtitle2" sx={{ minWidth: 24, mt: 1, fontWeight: 700 }}>
                  {option.optionLabel}
                </Typography>
                <TextField
                  size="small"
                  placeholder="Option text"
                  value={option.optionText}
                  onChange={(e) => updateOption(index, 'optionText', e.target.value)}
                  fullWidth
                />
                {options.length > 4 && (
                  <IconButton size="small" color="error" onClick={() => removeOption(index)}>
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                )}
              </Box>
              <TextField
                size="small"
                placeholder="Explanation for this option (optional)"
                value={option.explanation}
                onChange={(e) => updateOption(index, 'explanation', e.target.value)}
                fullWidth
                sx={{ ml: 4 }}
              />
            </Box>
          </Paper>
        ))}
      </RadioGroup>
      {options.length < 6 && (
        <Button startIcon={<AddIcon />} onClick={addOption} size="small" sx={{ mt: 1 }}>
          Add Option
        </Button>
      )}
    </Box>
  );
};

export default MCQOptionsEditor;
