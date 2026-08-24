import React, { useState } from 'react';
import { Box, Typography, Breadcrumbs, Link, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, LinearProgress, Alert, AlertTitle } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { bulkImportService } from '../../services';
import { BulkImportResult } from '../../types';

const BulkImportPage: React.FC = () => {
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

  const handleImport = async () => {
    if (!file) return;
    setImporting(true);
    setError('');
    try {
      const response = file.name.endsWith('.csv')
        ? await bulkImportService.importFromCsv(file)
        : await bulkImportService.importFromExcel(file);
      if (response.success) setResult(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Import failed.');
    } finally {
      setImporting(false);
    }
  };

  return (
    <Box>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/admin/dashboard" underline="hover" color="inherit">Dashboard</Link>
        <Typography color="text.primary">Bulk Import</Typography>
      </Breadcrumbs>

      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>
        Bulk Import Questions
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box
          sx={{
            border: '2px dashed',
            borderColor: file ? 'primary.main' : 'grey.400',
            borderRadius: 2,
            p: 4,
            textAlign: 'center',
            backgroundColor: file ? 'primary.50' : 'grey.50',
            mb: 2,
            cursor: 'pointer',
          }}
          component="label"
        >
          <input type="file" hidden accept=".xlsx,.xls,.csv" onChange={handleFileChange} />
          <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
          <Typography variant="body1" sx={{ mb: 0.5 }}>
            {file ? file.name : 'Drag & drop or click to browse'}
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

        <Button variant="contained" onClick={handleImport} disabled={!file || importing}>
          Start Import
        </Button>
      </Paper>

      {result && (
        <Paper sx={{ p: 3 }}>
          <Alert severity={result.failedImports === 0 ? 'success' : 'warning'} sx={{ mb: 2 }}>
            <AlertTitle>Import Complete</AlertTitle>
            Total: {result.totalRows} | Successful: {result.successfulImports} | Failed: {result.failedImports}
          </Alert>

          {result.errors.length > 0 && (
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>#</TableCell>
                    <TableCell>Error Message</TableCell>
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
        </Paper>
      )}
    </Box>
  );
};

export default BulkImportPage;
