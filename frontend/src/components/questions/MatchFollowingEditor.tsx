import React from 'react';
import { Box, TextField, Typography, Button, IconButton, Grid, Paper } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';

interface MatchPair {
  left: string;
  right: string;
}

interface MatchFollowingEditorProps {
  pairs: MatchPair[];
  onChange: (pairs: MatchPair[]) => void;
}

const MatchFollowingEditor: React.FC<MatchFollowingEditorProps> = ({ pairs, onChange }) => {
  const addPair = () => onChange([...pairs, { left: '', right: '' }]);

  const removePair = (index: number) => {
    if (pairs.length <= 2) return;
    onChange(pairs.filter((_, i) => i !== index));
  };

  const updatePair = (index: number, field: 'left' | 'right', value: string) => {
    const updated = pairs.map((p, i) => i === index ? { ...p, [field]: value } : p);
    onChange(updated);
  };

  return (
    <Box>
      <Typography variant="subtitle2" sx={{ mb: 1.5 }}>Match the Following Pairs</Typography>
      {pairs.map((pair, i) => (
        <Paper key={i} variant="outlined" sx={{ p: 1.5, mb: 1 }}>
          <Grid container spacing={1} alignItems="center">
            <Grid item xs={5}>
              <TextField size="small" fullWidth label={`Left ${i + 1}`} value={pair.left} onChange={(e) => updatePair(i, 'left', e.target.value)} />
            </Grid>
            <Grid item xs={1} sx={{ textAlign: 'center' }}>
              <Typography color="text.secondary">↔</Typography>
            </Grid>
            <Grid item xs={5}>
              <TextField size="small" fullWidth label={`Right ${i + 1}`} value={pair.right} onChange={(e) => updatePair(i, 'right', e.target.value)} />
            </Grid>
            <Grid item xs={1}>
              <IconButton size="small" color="error" onClick={() => removePair(i)} disabled={pairs.length <= 2}>
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Grid>
          </Grid>
        </Paper>
      ))}
      <Button startIcon={<AddIcon />} onClick={addPair} size="small" variant="outlined" sx={{ mt: 1 }}>
        Add Pair
      </Button>
    </Box>
  );
};

export default MatchFollowingEditor;
