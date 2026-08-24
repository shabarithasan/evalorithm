import React, { useState, useCallback } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
  AlertTitle,
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { bulkImportService } from '../../services';
import { BulkImportResult } from '../../types';

interface BulkImportDialogProps {
  open: boolean;
  onClose: () => void;
  onComplete: () => void;
}

const BulkImportDialog: React.FC<BulkImportDialogProps> = ({ open, onClose, onComplete }) => {
  const [file, setFile] = useState<File | null>(null);
  const [importing, setImporting] = useState(false);
  const [result, setResult] = useState<BulkImportResult | null>(null);
  const [error, setError] = useState('');

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files?.[0];
    if (selected) {
      setFile(selected);
      setResult(null);
      setError('');
    }
  };

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    const droppedFile = e.dataTransfer.files?.[0];
    if (droppedFile) {
      setFile(droppedFile);
      setResult(null);
      setError('');
    }
  }, []);

  const handleImport = async () => {
    if (!file) return;
    setImporting(true);
    setError('');
    try {
      const response = file.name.endsWith('.csv')
        ? await bulkImportService.importFromCsv(file)
        : await bulkImportService.importFromExcel(file);
      if (response.success) {
        setResult(response.data);
        if (response.data.failedImports === 0) {
          setTimeout(() => { onComplete(); onClose(); }, 1500);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Import failed. Please check the file format.');
    } finally {
      setImporting(false);
    }
  };

  const handleClose = () => {
    setFile(null);
    setResult(null);
    setError('');
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ fontWeight: 600 }}>Bulk Import Questions</DialogTitle>
      <DialogContent>
        <Box
          onDrop={handleDrop}
          onDragOver={(e) => e.preventDefault()}
          sx={{
            border: '2px dashed',
            borderColor: file ? 'primary.main' : 'grey.400',
            borderRadius: 2,
            p: 4,
            textAlign: 'center',
            backgroundColor: file ? 'primary.50' : 'grey.50',
            mb: 2,
            cursor: 'pointer',
            transition: 'all 0.2s',
            '&:hover': { borderColor: 'primary.main', backgroundColor: 'primary.50' },
          }}
          component="label"
        >
          <input type="file" hidden accept=".xlsx,.xls,.csv" onChange={handleFileChange} />
          <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
          <Typography variant="body1" sx={{ mb: 0.5 }}>
            {file ? file.name : 'Drag & drop a file or click to browse'}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Supported formats: Excel (.xlsx, .xls) or CSV (.csv)
          </Typography>
        </Box>

        {importing && <LinearProgress sx={{ mb: 2 }} />}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            <AlertTitle>Error</AlertTitle>
            {error}
          </Alert>
        )}

        {result && (
          <Box>
            <Alert severity={result.failedImports === 0 ? 'success' : 'warning'} sx={{ mb: 2 }}>
              <AlertTitle>Import Complete</AlertTitle>
              Total: {result.totalRows} | Successful: {result.successfulImports} | Failed: {result.failedImports}
            </Alert>
            {result.errors.length > 0 && (
              <TableContainer component={Paper} variant="outlined" sx={{ maxHeight: 200 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Row</TableCell>
                      <TableCell>Error</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {result.errors.map((err, i) => (
                      <TableRow key={i}>
                        <TableCell>{i + 1}</TableCell>
                        <TableCell>{err}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} variant="outlined" color="inherit">
          {result ? 'Close' : 'Cancel'}
        </Button>
        {!result && (
          <Button onClick={handleImport} variant="contained" disabled={!file || importing}>
            Import
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default BulkImportDialog;
