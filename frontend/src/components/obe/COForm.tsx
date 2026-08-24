import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  Button,
  MenuItem,
  Switch,
  FormControlLabel,
  Grid,
  Alert,
  CircularProgress,
  Autocomplete,
} from '@mui/material';
import { CourseOutcome, Subject } from '../../types';
import { obeService, subjectService } from '../../services';

interface COFormProps {
  initialData?: CourseOutcome;
  onSuccess: () => void;
  onCancel: () => void;
}

const bloomsLevels = [
  'K1_REMEMBER',
  'K2_UNDERSTAND',
  'K3_APPLY',
  'K4_ANALYZE',
  'K5_EVALUATE',
  'K6_CREATE',
];

const COForm: React.FC<COFormProps> = ({ initialData, onSuccess, onCancel }) => {
  const [code, setCode] = useState(initialData?.code || '');
  const [description, setDescription] = useState(initialData?.description || '');
  const [subjectId, setSubjectId] = useState<number | null>(initialData?.subjectId || null);
  const [bloomsLevel, setBloomsLevel] = useState(initialData?.bloomsLevel || 'K1_REMEMBER');
  const [isAttainable, setIsAttainable] = useState(initialData?.isAttainable ?? true);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [subjectSearch, setSubjectSearch] = useState('');

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const response = await subjectService.getAll();
      if (response.success) {
        setSubjects(response.data?.content || response.data || []);
      }
    } catch {
      // ignore
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!code || !description || !subjectId) {
      setError('Please fill all required fields');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const payload = { code, description, subjectId, bloomsLevel, isAttainable };
      if (initialData) {
        await obeService.updateCO(initialData.id, payload);
      } else {
        await obeService.createCO(payload);
      }
      onSuccess();
    } catch {
      setError('Failed to save Course Outcome');
    } finally {
      setLoading(false);
    }
  };

  const filteredSubjects = subjects.filter(
    (s) =>
      s.name.toLowerCase().includes(subjectSearch.toLowerCase()) ||
      s.code.toLowerCase().includes(subjectSearch.toLowerCase())
  );

  return (
    <Box component="form" onSubmit={handleSubmit}>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Grid container spacing={2}>
        <Grid item xs={12} sm={6}>
          <TextField
            label="CO Code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            fullWidth
            required
            placeholder="e.g., CO101.1"
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            label="Blooms Level"
            value={bloomsLevel}
            onChange={(e) => setBloomsLevel(e.target.value)}
            select
            fullWidth
          >
            {bloomsLevels.map((level) => (
              <MenuItem key={level} value={level}>
                {level}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={12}>
          <Autocomplete
            options={filteredSubjects}
            getOptionLabel={(option) => `${option.code} - ${option.name}`}
            onChange={(_, newValue) => setSubjectId(newValue?.id || null)}
            onInputChange={(_, value) => setSubjectSearch(value)}
            value={subjects.find((s) => s.id === subjectId) || null}
            renderInput={(params) => (
              <TextField {...params} label="Subject" fullWidth required />
            )}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            required
            multiline
            rows={3}
          />
        </Grid>
        <Grid item xs={12}>
          <FormControlLabel
            control={
              <Switch
                checked={isAttainable}
                onChange={(e) => setIsAttainable(e.target.checked)}
              />
            }
            label="Is Attainable"
          />
        </Grid>
      </Grid>
      <Box sx={{ display: 'flex', gap: 2, mt: 3, justifyContent: 'flex-end' }}>
        <Button onClick={onCancel}>Cancel</Button>
        <Button type="submit" variant="contained" disabled={loading}>
          {loading ? <CircularProgress size={20} /> : initialData ? 'Update' : 'Create'}
        </Button>
      </Box>
    </Box>
  );
};

export default COForm;
