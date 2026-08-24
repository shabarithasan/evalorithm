import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  Chip,
  Grid,
  TextField,
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import DownloadIcon from '@mui/icons-material/Download';
import VerifiedIcon from '@mui/icons-material/Verified';
import SearchIcon from '@mui/icons-material/Search';
import PageHeader from '../../components/common/PageHeader';
import CertificatePreview from '../../components/obe/CertificatePreview';
import { certificateService } from '../../services';
import { Certificate } from '../../types';

const StudentCertificatesPage: React.FC = () => {
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewCert, setPreviewCert] = useState<Certificate | null>(null);

  const [verifyNumber, setVerifyNumber] = useState('');
  const [verifyResult, setVerifyResult] = useState<Certificate | null>(null);
  const [verifyError, setVerifyError] = useState('');

  useEffect(() => {
    fetchCertificates();
  }, []);

  const fetchCertificates = async () => {
    try {
      const response = await certificateService.getMyCerts?.() || await certificateService.getAll();
      if (response.success) {
        setCertificates(response.data?.content || response.data || []);
      }
    } catch {
      setError('Failed to load certificates');
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (cert: Certificate) => {
    try {
      const response = await certificateService.download(cert.id);
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Certificate_${cert.certificateNumber}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch {
      setError('Failed to download certificate');
    }
  };

  const handleVerify = async () => {
    if (!verifyNumber) return;
    setVerifyError('');
    setVerifyResult(null);
    try {
      const response = await certificateService.verify(verifyNumber);
      if (response.success && response.data) {
        setVerifyResult(response.data);
      } else {
        setVerifyError('Certificate not found');
      }
    } catch {
      setVerifyError('Invalid certificate number');
    }
  };

  return (
    <Box>
      <PageHeader title="My Certificates" subtitle="View and download your certificates" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card variant="outlined" sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 1 }}>Verify Certificate</Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={8}>
              <TextField
                label="Certificate Number"
                value={verifyNumber}
                onChange={(e) => setVerifyNumber(e.target.value)}
                fullWidth
                size="small"
                placeholder="Enter certificate number"
                InputProps={{ startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} /> }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <Button variant="contained" onClick={handleVerify} fullWidth sx={{ height: 40 }}>
                <VerifiedIcon sx={{ mr: 1 }} /> Verify
              </Button>
            </Grid>
          </Grid>
          {verifyError && <Alert severity="error" sx={{ mt: 2 }}>{verifyError}</Alert>}
          {verifyResult && (
            <Alert severity="success" sx={{ mt: 2 }}>
              Certificate verified! Student: {verifyResult.studentName} | Type: {verifyResult.certificateType}
            </Alert>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>My Certificates ({certificates.length})</Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Certificate No.</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Type</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject/Exam</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Issued Date</TableCell>
                  <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {certificates.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      <Typography color="text.secondary" sx={{ py: 3 }}>
                        No certificates yet. Complete exams to earn certificates.
                      </Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  certificates.map((cert) => (
                    <TableRow key={cert.id} hover>
                      <TableCell><Typography fontWeight={600}>{cert.certificateNumber}</Typography></TableCell>
                      <TableCell>
                        <Chip
                          label={cert.certificateType}
                          size="small"
                          color={cert.certificateType === 'MERIT' ? 'warning' : 'primary'}
                        />
                      </TableCell>
                      <TableCell>{cert.examTitle || cert.subjectName || '-'}</TableCell>
                      <TableCell>{new Date(cert.issuedDate).toLocaleDateString()}</TableCell>
                      <TableCell align="right">
                        <Button size="small" startIcon={<VisibilityIcon />} onClick={() => { setPreviewCert(cert); setPreviewOpen(true); }}>
                          Preview
                        </Button>
                        <Button size="small" color="primary" startIcon={<DownloadIcon />} onClick={() => handleDownload(cert)}>
                          Download
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Preview Dialog */}
      <Dialog open={previewOpen} onClose={() => setPreviewOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Certificate Preview</DialogTitle>
        <DialogContent>
          {previewCert && <CertificatePreview certificate={previewCert} />}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPreviewOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default StudentCertificatesPage;
