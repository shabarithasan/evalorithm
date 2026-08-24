import React from 'react';
import { Box, TextField, Typography, FormControl, FormLabel, RadioGroup, FormControlLabel, Radio, Paper, Grid } from '@mui/material';

interface AssertionReasonData {
  assertion: string;
  reason: string;
  correctOption: string;
}

interface AssertionReasonEditorProps {
  data: AssertionReasonData;
  onChange: (data: AssertionReasonData) => void;
}

const options = [
  { value: 'A', label: 'Both assertion and reason are true, and reason is the correct explanation' },
  { value: 'B', label: 'Both assertion and reason are true, but reason is NOT the correct explanation' },
  { value: 'C', label: 'Assertion is true but reason is false' },
  { value: 'D', label: 'Assertion is false but reason is true' },
  { value: 'E', label: 'Both assertion and reason are false' },
];

const AssertionReasonEditor: React.FC<AssertionReasonEditorProps> = ({ data, onChange }) => {
  const update = (field: keyof AssertionReasonData, value: string) => {
    onChange({ ...data, [field]: value });
  };

  return (
    <Box>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <TextField fullWidth multiline rows={3} label="Assertion" value={data.assertion} onChange={(e) => update('assertion', e.target.value)} size="small" />
        </Grid>
        <Grid item xs={12}>
          <TextField fullWidth multiline rows={3} label="Reason" value={data.reason} onChange={(e) => update('reason', e.target.value)} size="small" />
        </Grid>
        <Grid item xs={12}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <FormControl component="fieldset">
              <FormLabel component="legend">Correct Answer</FormLabel>
              <RadioGroup value={data.correctOption} onChange={(e) => update('correctOption', e.target.value)}>
                {options.map((opt) => (
                  <FormControlLabel key={opt.value} value={opt.value} control={<Radio size="small" />} label={opt.label} sx={{ alignItems: 'flex-start', '& .MuiFormControlLabel-label': { fontSize: '0.875rem' } }} />
                ))}
              </RadioGroup>
            </FormControl>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AssertionReasonEditor;
