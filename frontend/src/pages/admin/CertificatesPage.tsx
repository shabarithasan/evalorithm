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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  Alert,
  IconButton,
  Chip,
  Tab,
  Tabs,
  InputAdornment,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import VisibilityIcon from '@mui/icons-material/Visibility';
import DownloadIcon from '@mui/icons-material/Download';
import VerifiedIcon from '@mui/icons-material/Verified';
import SearchIcon from '@mui/icons-material/Search';
import PageHeader from '../../components/common/PageHeader';
import CertificatePreview from '../../components/obe/CertificatePreview';
import { certificateService, studentService } from '../../services';
import { Certificate, CertificateTypeValue, Student } from '../../types';

const certTypes: { value: CertificateTypeValue; label: string }[] = [
  { value: 'COMPLETION', label: 'Certificate of Completion' },
  { value: 'MERIT', label: 'Certificate of Merit' },
  { value: 'PARTICIPATION', label: 'Certificate of Participation' },
];

const CertificatesPage: React.FC = () => {
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState(0);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Generate dialog
  const [genDialogOpen, setGenDialogOpen] = useState(false);
  const [genForm, setGenForm] = useState({
    certificateType: 'COMPLETION' as CertificateTypeValue,
    studentId: 0,
    examTitle: '',
    subjectName: '',
  });

  // Preview dialog
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewCert, setPreviewCert] = useState<Certificate | null>(null);

  // Verify
  const [verifyNumber, setVerifyNumber] = useState('');
  const [verifyResult, setVerifyResult] = useState<Certificate | null>(null);
  const [verifyError, setVerifyError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [certsRes, studsRes] = await Promise.allSettled([
        certificateService.getAll(),
        studentService.getAll(),
      ]);
      if (certsRes.status === 'fulfilled' && certsRes.value.success) {
        setCertificates(certsRes.value.data?.content || certsRes.value.data || []);
      }
      if (studsRes.status === 'fulfilled' && studsRes.value.success) {
        setStudents(studsRes.value.data?.content || studsRes.value.data || []);
      }
    } catch {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    if (!genForm.studentId) {
      setError('Please select a student');
      return;
    }
    try {
      await certificateService.generate(genForm);
      setGenDialogOpen(false);
      setGenForm({ certificateType: 'COMPLETION', studentId: 0, examTitle: '', subjectName: '' });
      setSuccess('Certificate generated successfully');
      fetchData();
    } catch {
      setError('Failed to generate certificate');
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

  const handleDownload = async (id: number) => {
    try {
      const response = await certificateService.download(id);
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Certificate_${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch {
      setError('Failed to download certificate');
    }
  };

  return (
    <Box>
      <PageHeader title="Certificates" subtitle="Generate, manage, and verify certificates" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
            <Tab label="All Certificates" />
            <Tab label="Verify Certificate" />
          </Tabs>

          {tabValue === 0 && (
            <Box sx={{ pt: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => setGenDialogOpen(true)}>
                  Generate Certificate
                </Button>
              </Box>
              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow sx={{ backgroundColor: 'primary.main' }}>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Cert No.</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Student</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Type</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject/Exam</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Issued Date</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Issued By</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {certificates.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={7} align="center">
                          <Typography color="text.secondary" sx={{ py: 2 }}>No certificates generated yet</Typography>
                        </TableCell>
                      </TableRow>
                    ) : (
                      certificates.map((cert) => (
                        <TableRow key={cert.id} hover>
                          <TableCell><Typography fontWeight={600} variant="body2">{cert.certificateNumber}</Typography></TableCell>
                          <TableCell>{cert.studentName}</TableCell>
                          <TableCell>
                            <Chip
                              label={cert.certificateType}
                              size="small"
                              color={cert.certificateType === 'MERIT' ? 'warning' : 'primary'}
                            />
                          </TableCell>
                          <TableCell>{cert.examTitle || cert.subjectName || '-'}</TableCell>
                          <TableCell>{new Date(cert.issuedDate).toLocaleDateString()}</TableCell>
                          <TableCell>{cert.issuedByName}</TableCell>
                          <TableCell align="right">
                            <IconButton size="small" onClick={() => { setPreviewCert(cert); setPreviewOpen(true); }}>
                              <VisibilityIcon fontSize="small" />
                            </IconButton>
                            <IconButton size="small" color="primary" onClick={() => handleDownload(cert.id)}>
                              <DownloadIcon fontSize="small" />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}

          {tabValue === 1 && (
            <Box sx={{ pt: 2 }}>
              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={12} sm={8}>
                  <TextField
                    label="Certificate Number"
                    value={verifyNumber}
                    onChange={(e) => setVerifyNumber(e.target.value)}
                    fullWidth
                    placeholder="Enter certificate number"
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Button variant="contained" onClick={handleVerify} fullWidth sx={{ height: 56 }}>
                    <VerifiedIcon sx={{ mr: 1 }} /> Verify
                  </Button>
                </Grid>
              </Grid>
              {verifyError && <Alert severity="error" sx={{ mb: 2 }}>{verifyError}</Alert>}
              {verifyResult && (
                <Card variant="outlined">
                  <CardContent>
                    <Alert severity="success" sx={{ mb: 2 }}>Certificate Verified Successfully!</Alert>
                    <Grid container spacing={2}>
                      <Grid item xs={6}><Typography variant="body2" color="text.secondary">Certificate No:</Typography><Typography fontWeight={600}>{verifyResult.certificateNumber}</Typography></Grid>
                      <Grid item xs={6}><Typography variant="body2" color="text.secondary">Student:</Typography><Typography fontWeight={600}>{verifyResult.studentName}</Typography></Grid>
                      <Grid item xs={6}><Typography variant="body2" color="text.secondary">Type:</Typography><Typography fontWeight={600}>{verifyResult.certificateType}</Typography></Grid>
                      <Grid item xs={6}><Typography variant="body2" color="text.secondary">Issued Date:</Typography><Typography fontWeight={600}>{new Date(verifyResult.issuedDate).toLocaleDateString()}</Typography></Grid>
                      <Grid item xs={6}><Typography variant="body2" color="text.secondary">Issued By:</Typography><Typography fontWeight={600}>{verifyResult.issuedByName}</Typography></Grid>
                    </Grid>
                  </CardContent>
                </Card>
              )}
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Generate Dialog */}
      <Dialog open={genDialogOpen} onClose={() => setGenDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Generate Certificate</DialogTitle>
        <DialogContent sx={{ pt: '16px !important' }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                select
                label="Certificate Type"
                value={genForm.certificateType}
                onChange={(e) => setGenForm({ ...genForm, certificateType: e.target.value as CertificateTypeValue })}
                fullWidth
              >
                {certTypes.map((ct) => (
                  <MenuItem key={ct.value} value={ct.value}>{ct.label}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                label="Student"
                value={genForm.studentId || ''}
                onChange={(e) => setGenForm({ ...genForm, studentId: Number(e.target.value) })}
                fullWidth
                required
              >
                {students.map((s) => (
                  <MenuItem key={s.id} value={s.id}>
                    {s.firstName} {s.lastName} ({s.registerNumber})
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Exam Title (optional)"
                value={genForm.examTitle}
                onChange={(e) => setGenForm({ ...genForm, examTitle: e.target.value })}
                fullWidth
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Subject Name (optional)"
                value={genForm.subjectName}
                onChange={(e) => setGenForm({ ...genForm, subjectName: e.target.value })}
                fullWidth
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setGenDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleGenerate}>Generate</Button>
        </DialogActions>
      </Dialog>

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

export default CertificatesPage;
