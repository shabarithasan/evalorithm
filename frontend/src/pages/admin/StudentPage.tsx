import React, { useState, useEffect, useCallback } from 'react';
import { GridColDef, GridPaginationModel, GridValueGetterParams } from '@mui/x-data-grid';
import {
  IconButton,
  Tooltip,
  Box,
  MenuItem,
  TextField,
  Chip,
  Typography,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import FormDialog from '../../components/common/FormDialog';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { studentService, departmentService, semesterService } from '../../services';
import { Student, StudentRequest, Department, Semester } from '../../types';

const StudentPage: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [enrollDialogOpen, setEnrollDialogOpen] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [selectedSubjectIds, setSelectedSubjectIds] = useState<number[]>([]);
  const [allSubjects, setAllSubjects] = useState<any[]>([]);
  const [formData, setFormData] = useState<StudentRequest>({
    registerNumber: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    departmentId: 0,
    semesterId: 0,
    enrolledSubjectIds: [],
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchDropdowns();
  }, []);

  const fetchDropdowns = async () => {
    try {
      const [deptRes, semRes] = await Promise.all([
        departmentService.getAll(0, 100),
        semesterService.getAll(0, 100),
      ]);
      if (deptRes.success) setDepartments(deptRes.data.content);
      if (semRes.success) setSemesters(semRes.data.content);
    } catch {
      // Handle error
    }
  };

  const fetchStudents = useCallback(async () => {
    setLoading(true);
    try {
      const response = await studentService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc'
      );
      if (response.success) {
        setStudents(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchStudents();
  }, [fetchStudents]);

  const handleOpenDialog = (student?: Student) => {
    if (student) {
      setSelectedStudent(student);
      setFormData({
        registerNumber: student.registerNumber,
        firstName: student.firstName,
        lastName: student.lastName,
        email: student.email,
        phone: student.phone,
        departmentId: student.departmentId,
        semesterId: student.semesterId,
        enrolledSubjectIds: student.enrolledSubjects?.map((s) => s.id) || [],
      });
    } else {
      setSelectedStudent(null);
      setFormData({
        registerNumber: '',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        departmentId: departments[0]?.id || 0,
        semesterId: semesters[0]?.id || 0,
        enrolledSubjectIds: [],
      });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedStudent) {
        await studentService.update(selectedStudent.id, formData);
      } else {
        await studentService.create(formData);
      }
      setDialogOpen(false);
      fetchStudents();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedStudent) return;
    try {
      await studentService.delete(selectedStudent.id);
      setConfirmOpen(false);
      setSelectedStudent(null);
      fetchStudents();
    } catch {
      // Handle error
    }
  };

  const handleEnrollSubjects = async () => {
    if (!selectedStudent) return;
    setSubmitting(true);
    try {
      const currentIds = selectedStudent.enrolledSubjects?.map((s) => s.id) || [];
      const toAdd = selectedSubjectIds.filter((id) => !currentIds.includes(id));
      const toRemove = currentIds.filter((id) => !selectedSubjectIds.includes(id));

      if (toAdd.length > 0) {
        await studentService.enrollSubjects(selectedStudent.id, toAdd);
      }
      if (toRemove.length > 0) {
        await studentService.unenrollSubjects(selectedStudent.id, toRemove);
      }
      setEnrollDialogOpen(false);
      fetchStudents();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const columns: GridColDef[] = [
    { field: 'registerNumber', headerName: 'Register No', width: 140 },
    {
      field: 'name',
      headerName: 'Name',
      flex: 1,
      minWidth: 180,
      valueGetter: (params: GridValueGetterParams) => `${params.row.firstName} ${params.row.lastName}`,
    },
    { field: 'email', headerName: 'Email', flex: 1, minWidth: 200 },
    { field: 'departmentName', headerName: 'Department', width: 150 },
    { field: 'semesterNumber', headerName: 'Semester', width: 100, type: 'number' },
    {
      field: 'actions',
      headerName: 'Actions',
      width: 160,
      sortable: false,
      renderCell: (params) => (
        <Box>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => handleOpenDialog(params.row)}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Enroll Subjects">
            <IconButton
              size="small"
              color="primary"
              onClick={() => {
                setSelectedStudent(params.row);
                setSelectedSubjectIds(params.row.enrolledSubjects?.map((s: any) => s.id) || []);
                setEnrollDialogOpen(true);
              }}
            >
              <HowToRegIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              color="error"
              onClick={() => {
                setSelectedStudent(params.row);
                setConfirmOpen(true);
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Students"
        subtitle="Manage student records"
        actionLabel="Add Student"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={students}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedStudent ? 'Edit Student' : 'Add Student'}
        onSubmit={handleSubmit}
        loading={submitting}
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <TextField
            label="Register Number"
            value={formData.registerNumber}
            onChange={(e) => setFormData({ ...formData, registerNumber: e.target.value })}
            required
            fullWidth
          />
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="First Name"
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Last Name"
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Phone"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              select
              label="Department"
              value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: Number(e.target.value) })}
              required
              fullWidth
            >
              {departments.map((dept) => (
                <MenuItem key={dept.id} value={dept.id}>
                  {dept.name}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Semester"
              value={formData.semesterId}
              onChange={(e) => setFormData({ ...formData, semesterId: Number(e.target.value) })}
              required
              fullWidth
            >
              {semesters.map((sem) => (
                <MenuItem key={sem.id} value={sem.id}>
                  {sem.departmentName} - Semester {sem.number}
                </MenuItem>
              ))}
            </TextField>
          </Box>
        </Box>
      </FormDialog>

      <FormDialog
        open={enrollDialogOpen}
        onClose={() => setEnrollDialogOpen(false)}
        title={`Enroll Subjects for ${selectedStudent?.firstName} ${selectedStudent?.lastName}`}
        onSubmit={handleEnrollSubjects}
        loading={submitting}
        maxWidth="md"
        submitLabel="Update Enrollment"
      >
        <Box sx={{ pt: 1 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Select the subjects to enroll this student in.
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {selectedStudent?.enrolledSubjects?.map((subject) => (
              <Chip
                key={subject.id}
                label={`${subject.code} - ${subject.name}`}
                onClick={() => {
                  setSelectedSubjectIds((prev) =>
                    prev.includes(subject.id)
                      ? prev.filter((id) => id !== subject.id)
                      : [...prev, subject.id]
                  );
                }}
                color={selectedSubjectIds.includes(subject.id) ? 'primary' : 'default'}
                variant={selectedSubjectIds.includes(subject.id) ? 'filled' : 'outlined'}
                sx={{ cursor: 'pointer' }}
              />
            ))}
            {(!selectedStudent?.enrolledSubjects || selectedStudent.enrolledSubjects.length === 0) && (
              <Typography variant="body2" color="text.secondary">
                No subjects currently enrolled. Select subjects to add enrollment.
              </Typography>
            )}
          </Box>
        </Box>
      </FormDialog>

      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Student"
        message={`Are you sure you want to delete "${selectedStudent?.firstName} ${selectedStudent?.lastName}"?`}
      />
    </Box>
  );
};

export default StudentPage;
