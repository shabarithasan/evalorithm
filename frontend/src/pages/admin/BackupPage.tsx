import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Switch,
  FormControlLabel,
  CircularProgress,
} from '@mui/material';
import BackupIcon from '@mui/icons-material/Backup';
import RestoreIcon from '@mui/icons-material/Restore';
import DeleteIcon from '@mui/icons-material/Delete';
import DownloadIcon from '@mui/icons-material/Download';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { backupService } from '../../services';
import { Backup } from '../../types';

const BackupPage: React.FC = () => {
  const [backups, setBackups] = useState<Backup[]>([]);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [autoBackup, setAutoBackup] = useState(true);
  const [restoreDialog, setRestoreDialog] = useState<Backup | null>(null);
  const [deleteDialog, setDeleteDialog] = useState<Backup | null>(null);

  useEffect(() => {
    fetchBackups();
  }, []);

  const fetchBackups = async () => {
    try {
      const response = await backupService.getAll();
      if (response.success) {
        setBackups(response.data?.content || response.data || []);
      }
    } catch {
      setError('Failed to load backups');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateBackup = async () => {
    setCreating(true);
    setError('');
    try {
      await backupService.create();
      setSuccess('Backup created successfully');
      fetchBackups();
    } catch {
      setError('Failed to create backup');
    } finally {
      setCreating(false);
    }
  };

  const handleRestore = async (backup: Backup) => {
    try {
      await backupService.restore(backup.id);
      setSuccess('Backup restored successfully');
      setRestoreDialog(null);
    } catch {
      setError('Failed to restore backup');
    }
  };

  const handleDelete = async (backup: Backup) => {
    try {
      await backupService.delete(backup.id);
      setSuccess('Backup deleted');
      setDeleteDialog(null);
      fetchBackups();
    } catch {
      setError('Failed to delete backup');
    }
  };

  const handleDownload = async (backup: Backup) => {
    try {
      const response = await backupService.download(backup.id);
      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = backup.fileName;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch {
      setError('Failed to download backup');
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  const getStatusColor = (status: string): 'success' | 'error' | 'warning' | 'info' => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'FAILED': return 'error';
      case 'IN_PROGRESS': return 'warning';
      default: return 'info';
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="Backup & Recovery" subtitle="Manage system backups and restoration" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Create Backup</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Create a manual backup of the entire system database.
              </Typography>
              <Button
                variant="contained"
                startIcon={creating ? <CircularProgress size={20} color="inherit" /> : <BackupIcon />}
                onClick={handleCreateBackup}
                disabled={creating}
                fullWidth
              >
                {creating ? 'Creating Backup...' : 'Create Backup Now'}
              </Button>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Auto Backup</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Automatically backup system data every 24 hours.
              </Typography>
              <FormControlLabel
                control={<Switch checked={autoBackup} onChange={(e) => setAutoBackup(e.target.checked)} />}
                label="Enable Auto Backup"
              />
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>Backup History</Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>File Name</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Type</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Size</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Status</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Created By</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Date</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {backups.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      <Typography color="text.secondary" sx={{ py: 2 }}>No backups found</Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  backups.map((backup) => (
                    <TableRow key={backup.id} hover>
                      <TableCell><Typography fontWeight={600} variant="body2">{backup.fileName}</Typography></TableCell>
                      <TableCell><Chip label={backup.backupType} size="small" variant="outlined" /></TableCell>
                      <TableCell>{formatFileSize(backup.fileSize)}</TableCell>
                      <TableCell><Chip label={backup.status} size="small" color={getStatusColor(backup.status)} /></TableCell>
                      <TableCell>{backup.createdByName || '-'}</TableCell>
                      <TableCell>{new Date(backup.createdAt).toLocaleString()}</TableCell>
                      <TableCell align="right">
                        <Button size="small" startIcon={<DownloadIcon />} onClick={() => handleDownload(backup)}>Download</Button>
                        <Button size="small" color="warning" startIcon={<RestoreIcon />} onClick={() => setRestoreDialog(backup)}>Restore</Button>
                        <Button size="small" color="error" startIcon={<DeleteIcon />} onClick={() => setDeleteDialog(backup)}>Delete</Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Restore Dialog */}
      <Dialog open={!!restoreDialog} onClose={() => setRestoreDialog(null)}>
        <DialogTitle>Confirm Restore</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to restore backup <strong>{restoreDialog?.fileName}</strong>? This will overwrite the current database.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRestoreDialog(null)}>Cancel</Button>
          <Button variant="contained" color="warning" onClick={() => restoreDialog && handleRestore(restoreDialog)}>
            Restore
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={!!deleteDialog} onClose={() => setDeleteDialog(null)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete backup <strong>{deleteDialog?.fileName}</strong>? This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog(null)}>Cancel</Button>
          <Button variant="contained" color="error" onClick={() => deleteDialog && handleDelete(deleteDialog)}>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default BackupPage;
