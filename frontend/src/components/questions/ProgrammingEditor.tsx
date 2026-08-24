import React from 'react';
import {
  Box,
  TextField,
  Typography,
  MenuItem,
  IconButton,
  Paper,
  Button,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { ProgrammingQuestionData } from '../../types';

interface ProgrammingEditorProps {
  data: ProgrammingQuestionData;
  onChange: (data: ProgrammingQuestionData) => void;
}

const languages = ['C', 'C++', 'Java', 'Python', 'JavaScript', 'TypeScript', 'Go', 'Rust', 'Kotlin', 'Swift'];

const ProgrammingEditor: React.FC<ProgrammingEditorProps> = ({ data, onChange }) => {
  const update = (field: keyof ProgrammingQuestionData, value: string) => {
    onChange({ ...data, [field]: value });
  };

  const testCases = data.testCases ? JSON.parse(data.testCases || '[]') : [];

  const updateTestCases = (cases: any[]) => {
    onChange({ ...data, testCases: JSON.stringify(cases) });
  };

  const addTestCase = () => {
    updateTestCases([...testCases, { input: '', output: '', hidden: false }]);
  };

  const removeTestCase = (index: number) => {
    updateTestCases(testCases.filter((_: any, i: number) => i !== index));
  };

  const updateTestCase = (index: number, field: string, value: any) => {
    const updated = [...testCases];
    updated[index] = { ...updated[index], [field]: value };
    updateTestCases(updated);
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Typography variant="subtitle2">Programming Question</Typography>

      <TextField
        select
        label="Programming Language"
        value={data.programmingLanguage}
        onChange={(e) => update('programmingLanguage', e.target.value)}
        fullWidth
        size="small"
      >
        {languages.map((lang) => (
          <MenuItem key={lang} value={lang}>{lang}</MenuItem>
        ))}
      </TextField>

      <TextField
        label="Problem Statement"
        value={data.problemStatement}
        onChange={(e) => update('problemStatement', e.target.value)}
        multiline
        rows={4}
        fullWidth
        size="small"
      />

      <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
        <TextField
          label="Input Format"
          value={data.inputFormat}
          onChange={(e) => update('inputFormat', e.target.value)}
          multiline
          rows={3}
          fullWidth
          size="small"
        />
        <TextField
          label="Output Format"
          value={data.outputFormat}
          onChange={(e) => update('outputFormat', e.target.value)}
          multiline
          rows={3}
          fullWidth
          size="small"
        />
      </Box>

      <TextField
        label="Constraints"
        value={data.constraints}
        onChange={(e) => update('constraints', e.target.value)}
        multiline
        rows={2}
        fullWidth
        size="small"
      />

      <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
        <TextField
          label="Sample Input"
          value={data.sampleInput}
          onChange={(e) => update('sampleInput', e.target.value)}
          multiline
          rows={3}
          fullWidth
          size="small"
        />
        <TextField
          label="Sample Output"
          value={data.sampleOutput}
          onChange={(e) => update('sampleOutput', e.target.value)}
          multiline
          rows={3}
          fullWidth
          size="small"
        />
      </Box>

      <Box>
        <Typography variant="subtitle2" sx={{ mb: 1 }}>Test Cases</Typography>
        {testCases.map((tc: any, i: number) => (
          <Paper key={i} variant="outlined" sx={{ p: 2, mb: 1.5 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
              <Typography variant="caption" fontWeight={600}>Test Case {i + 1}</Typography>
              <IconButton size="small" color="error" onClick={() => removeTestCase(i)}>
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Box>
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 1.5 }}>
              <TextField
                size="small"
                label="Input"
                value={tc.input}
                onChange={(e) => updateTestCase(i, 'input', e.target.value)}
                multiline
                rows={2}
                fullWidth
              />
              <TextField
                size="small"
                label="Expected Output"
                value={tc.output}
                onChange={(e) => updateTestCase(i, 'output', e.target.value)}
                multiline
                rows={2}
                fullWidth
              />
            </Box>
          </Paper>
        ))}
        <Button startIcon={<AddIcon />} onClick={addTestCase} size="small">
          Add Test Case
        </Button>
      </Box>

      <TextField
        label="Starter Code"
        value={data.starterCode}
        onChange={(e) => update('starterCode', e.target.value)}
        multiline
        rows={5}
        fullWidth
        size="small"
        sx={{ fontFamily: 'monospace' }}
      />

      <TextField
        label="Solution Code"
        value={data.solutionCode}
        onChange={(e) => update('solutionCode', e.target.value)}
        multiline
        rows={5}
        fullWidth
        size="small"
        sx={{ fontFamily: 'monospace' }}
      />
    </Box>
  );
};

export default ProgrammingEditor;
