import React, { useState, useCallback, useEffect } from 'react';
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
  Chip,
  List,
  ListItem,
  ListItemText,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { syllabusUploadService, departmentService, semesterService, subjectService } from '../../services';
import { SyllabusUploadResult, Department, Semester, Subject } from '../../types';

interface SyllabusUploadDialogProps {
  open: boolean;
  onClose: () => void;
  onComplete: () => void;
}

const SyllabusUploadDialog: React.FC<SyllabusUploadDialogProps> = ({ open, onClose, onComplete }) => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState<SyllabusUploadResult | null>(null);
  const [error, setError] = useState('');

  const [departments, setDepartments] = useState<Department[]>([]);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [selectedDept, setSelectedDept] = useState<number | ''>('');
  const [selectedSem, setSelectedSem] = useState<number | ''>('');
  const [selectedSubj, setSelectedSubj] = useState<number | ''>('');

  useEffect(() => {
    if (!open) return;
    departmentService.getAll(0, 100).then(res => {
      if (res.success) setDepartments(res.data.content);
    }).catch(() => {});
  }, [open]);

  const loadSemesters = useCallback((deptId: number) => {
    semesterService.getByDepartment(deptId).then(res => {
      if (res.success) setSemesters(res.data);
    }).catch(() => {});
  }, []);

  const loadSubjects = useCallback((semId: number) => {
    subjectService.getBySemester(semId).then(res => {
      if (res.success) setSubjects(res.data);
    }).catch(() => {});
  }, []);

  useEffect(() => {
    if (selectedDept) {
      setSelectedSem('');
      setSelectedSubj('');
      setSubjects([]);
      loadSemesters(selectedDept as number);
    }
  }, [selectedDept, loadSemesters]);

  useEffect(() => {
    if (selectedSem) {
      setSelectedSubj('');
      loadSubjects(selectedSem as number);
    }
  }, [selectedSem, loadSubjects]);

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

  const handleUpload = async () => {
    if (!file || !selectedDept || !selectedSem || !selectedSubj) return;
    setUploading(true);
    setError('');
    try {
      const response = await syllabusUploadService.uploadSyllabus(file, selectedDept as number, selectedSem as number, selectedSubj as number);
      if (response.success) {
        setResult(response.data);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Upload failed. Please check the file format.');
    } finally {
      setUploading(false);
    }
  };

  const handleConfirm = () => {
    onComplete();
    handleClose();
  };

  const handleClose = () => {
    setFile(null);
    setResult(null);
    setError('');
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ fontWeight: 600 }}>Upload Syllabus</DialogTitle>
      <DialogContent>
        {!result && (
          <Box>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <FormControl fullWidth size="small">
                <InputLabel>Department</InputLabel>
                <Select value={selectedDept} label="Department" onChange={e => setSelectedDept(e.target.value as number)}>
                  {departments.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl fullWidth size="small" disabled={!selectedDept}>
                <InputLabel>Semester</InputLabel>
                <Select value={selectedSem} label="Semester" onChange={e => setSelectedSem(e.target.value as number)}>
                  {semesters.map(s => <MenuItem key={s.id} value={s.id}>Semester {s.number}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl fullWidth size="small" disabled={!selectedSem}>
                <InputLabel>Subject</InputLabel>
                <Select value={selectedSubj} label="Subject" onChange={e => setSelectedSubj(e.target.value as number)}>
                  {subjects.map(s => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
                </Select>
              </FormControl>
            </Box>
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
              <input type="file" hidden accept=".pdf,.docx,.doc" onChange={handleFileChange} />
              <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
              <Typography variant="body1" sx={{ mb: 0.5 }}>
                {file ? file.name : 'Drag & drop a syllabus file or click to browse'}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Supported formats: PDF, DOCX
              </Typography>
            </Box>
          </Box>
        )}

        {uploading && <LinearProgress sx={{ mb: 2 }} />}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            <AlertTitle>Error</AlertTitle>
            {error}
          </Alert>
        )}

        {result && (
          <Box>
            <Alert severity="success" sx={{ mb: 2 }}>{result.message}</Alert>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>
              Extracted Topics
            </Typography>
            {result.extractedTopics.map((topic, i) => (
              <Paper key={i} variant="outlined" sx={{ p: 2, mb: 1.5 }}>
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                  Unit {topic.unitNumber}: {topic.unitName}
                </Typography>
                <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                  {topic.topics.map((t, j) => (
                    <Chip key={j} label={t} size="small" variant="outlined" />
                  ))}
                </Box>
              </Paper>
            ))}
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} variant="outlined" color="inherit">
          {result ? 'Close' : 'Cancel'}
        </Button>
        {!result && (
          <Button onClick={handleUpload} variant="contained" disabled={!file || !selectedDept || !selectedSem || !selectedSubj || uploading}>
            Upload & Generate
          </Button>
        )}
        {result && (
          <Button onClick={handleConfirm} variant="contained">
            Confirm & Save
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default SyllabusUploadDialog;
