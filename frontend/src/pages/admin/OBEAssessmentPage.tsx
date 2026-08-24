import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  Chip,
  Grid,
  TextField,
  MenuItem,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import LinkIcon from '@mui/icons-material/Link';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import COForm from '../../components/obe/COForm';
import AttainmentTable from '../../components/obe/AttainmentTable';
import { obeService, subjectService, departmentService } from '../../services';
import {
  CourseOutcome,
  ProgramOutcome,
  ProgramSpecificOutcome,
  Attainment,
  Department,
  Subject,
} from '../../types';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index }) => (
  <div role="tabpanel" hidden={value !== index}>
    {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
  </div>
);

const OBEAssessmentPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(true);
  const [cos, setCos] = useState<CourseOutcome[]>([]);
  const [pos, setPos] = useState<ProgramOutcome[]>([]);
  const [psos, setPsos] = useState<ProgramSpecificOutcome[]>([]);
  const [attainments, setAttainments] = useState<Attainment[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [selectedDept, setSelectedDept] = useState<number>(0);
  const [selectedSubject, setSelectedSubject] = useState<number>(0);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // CO dialog
  const [coDialogOpen, setCoDialogOpen] = useState(false);
  const [editingCO, setEditingCO] = useState<CourseOutcome | undefined>();

  // PO dialog
  const [poDialogOpen, setPoDialogOpen] = useState(false);
  const [poForm, setPoForm] = useState({ code: '', name: '', description: '', departmentId: 0 });

  // PSO dialog
  const [psoDialogOpen, setPsoDialogOpen] = useState(false);
  const [psoForm, setPsoForm] = useState({ code: '', name: '', description: '', departmentId: 0 });

  // Mapping dialog
  const [mappingDialogOpen, setMappingDialogOpen] = useState(false);
  const [mappingForm, setMappingForm] = useState({ coId: 0, questionId: 0 });

  // Delete dialog
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ type: string; id: number } | null>(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [deptsRes, cosRes, posRes, psosRes, attRes, subRes] = await Promise.allSettled([
        departmentService.getAll(),
        obeService.getAllCOs(),
        obeService.getAllPOs(),
        obeService.getAllPSOs(),
        obeService.getAllAttainments(),
        subjectService.getAll(),
      ]);
      if (deptsRes.status === 'fulfilled' && deptsRes.value.success) {
        setDepartments(deptsRes.value.data?.content || deptsRes.value.data || []);
      }
      if (cosRes.status === 'fulfilled' && cosRes.value.success) {
        setCos(cosRes.value.data?.content || cosRes.value.data || []);
      }
      if (posRes.status === 'fulfilled' && posRes.value.success) {
        setPos(posRes.value.data?.content || posRes.value.data || []);
      }
      if (psosRes.status === 'fulfilled' && psosRes.value.success) {
        setPsos(psosRes.value.data?.content || psosRes.value.data || []);
      }
      if (attRes.status === 'fulfilled' && attRes.value.success) {
        setAttainments(attRes.value.data?.content || attRes.value.data || []);
      }
      if (subRes.status === 'fulfilled' && subRes.value.success) {
        setSubjects(subRes.value.data?.content || subRes.value.data || []);
      }
    } catch {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      if (deleteTarget.type === 'CO') await obeService.deleteCO(deleteTarget.id);
      else if (deleteTarget.type === 'PO') await obeService.deletePO(deleteTarget.id);
      else if (deleteTarget.type === 'PSO') await obeService.deletePSO(deleteTarget.id);
      setSuccess(`${deleteTarget.type} deleted successfully`);
      setDeleteDialogOpen(false);
      setDeleteTarget(null);
      fetchData();
    } catch {
      setError(`Failed to delete ${deleteTarget.type}`);
    }
  };

  const handleCreatePO = async () => {
    if (!poForm.code || !poForm.name || !poForm.departmentId) {
      setError('Please fill all PO fields');
      return;
    }
    try {
      await obeService.createPO(poForm);
      setPoDialogOpen(false);
      setPoForm({ code: '', name: '', description: '', departmentId: 0 });
      setSuccess('PO created successfully');
      fetchData();
    } catch {
      setError('Failed to create PO');
    }
  };

  const handleCreatePSO = async () => {
    if (!psoForm.code || !psoForm.name || !psoForm.departmentId) {
      setError('Please fill all PSO fields');
      return;
    }
    try {
      await obeService.createPSO(psoForm);
      setPsoDialogOpen(false);
      setPsoForm({ code: '', name: '', description: '', departmentId: 0 });
      setSuccess('PSO created successfully');
      fetchData();
    } catch {
      setError('Failed to create PSO');
    }
  };

  const handleMapQuestion = async () => {
    if (!mappingForm.coId || !mappingForm.questionId) {
      setError('Please select CO and Question');
      return;
    }
    try {
      await obeService.mapQuestion(mappingForm);
      setMappingDialogOpen(false);
      setMappingForm({ coId: 0, questionId: 0 });
      setSuccess('Question mapped to CO successfully');
    } catch {
      setError('Failed to map question');
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="OBE Assessment" subtitle="Outcome-Based Education management" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)} variant="scrollable" scrollButtons="auto">
            <Tab label="Course Outcomes" />
            <Tab label="Program Outcomes" />
            <Tab label="PSOs" />
            <Tab label="CO Mapping" />
            <Tab label="Attainment Dashboard" />
          </Tabs>

          <TabPanel value={tabValue} index={0}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">Course Outcomes</Typography>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => { setEditingCO(undefined); setCoDialogOpen(true); }}
              >
                Add CO
              </Button>
            </Box>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'primary.main' }}>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Code</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Description</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Blooms Level</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Mappings</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Attainable</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {cos.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center">
                        <Typography color="text.secondary" sx={{ py: 2 }}>No Course Outcomes defined</Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    cos.map((co) => (
                      <TableRow key={co.id} hover>
                        <TableCell><Typography fontWeight={600}>{co.code}</Typography></TableCell>
                        <TableCell>{co.description}</TableCell>
                        <TableCell>{co.subjectName}</TableCell>
                        <TableCell><Chip label={co.bloomsLevel} size="small" variant="outlined" /></TableCell>
                        <TableCell>{co.mappingCount}</TableCell>
                        <TableCell>
                          <Chip label={co.isAttainable ? 'Yes' : 'No'} color={co.isAttainable ? 'success' : 'default'} size="small" />
                        </TableCell>
                        <TableCell align="right">
                          <IconButton size="small" onClick={() => { setEditingCO(co); setCoDialogOpen(true); }}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                          <IconButton size="small" color="error" onClick={() => { setDeleteTarget({ type: 'CO', id: co.id }); setDeleteDialogOpen(true); }}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">Program Outcomes</Typography>
              <Button variant="contained" startIcon={<AddIcon />} onClick={() => setPoDialogOpen(true)}>
                Add PO
              </Button>
            </Box>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'primary.main' }}>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Code</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Name</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Description</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Department</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Mappings</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pos.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={6} align="center">
                        <Typography color="text.secondary" sx={{ py: 2 }}>No Program Outcomes defined</Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    pos.map((po) => (
                      <TableRow key={po.id} hover>
                        <TableCell><Typography fontWeight={600}>{po.code}</Typography></TableCell>
                        <TableCell>{po.name}</TableCell>
                        <TableCell>{po.description}</TableCell>
                        <TableCell>{po.departmentName}</TableCell>
                        <TableCell>{po.mappingCount}</TableCell>
                        <TableCell align="right">
                          <IconButton size="small" color="error" onClick={() => { setDeleteTarget({ type: 'PO', id: po.id }); setDeleteDialogOpen(true); }}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">Program Specific Outcomes</Typography>
              <Button variant="contained" startIcon={<AddIcon />} onClick={() => setPsoDialogOpen(true)}>
                Add PSO
              </Button>
            </Box>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'primary.main' }}>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Code</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Name</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Description</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Department</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Mappings</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {psos.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={6} align="center">
                        <Typography color="text.secondary" sx={{ py: 2 }}>No PSOs defined</Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    psos.map((pso) => (
                      <TableRow key={pso.id} hover>
                        <TableCell><Typography fontWeight={600}>{pso.code}</Typography></TableCell>
                        <TableCell>{pso.name}</TableCell>
                        <TableCell>{pso.description}</TableCell>
                        <TableCell>{pso.departmentName}</TableCell>
                        <TableCell>{pso.mappingCount}</TableCell>
                        <TableCell align="right">
                          <IconButton size="small" color="error" onClick={() => { setDeleteTarget({ type: 'PSO', id: pso.id }); setDeleteDialogOpen(true); }}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={3}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">CO - Question Mapping</Typography>
              <Button variant="contained" startIcon={<LinkIcon />} onClick={() => setMappingDialogOpen(true)}>
                Map Question
              </Button>
            </Box>
            <Alert severity="info" sx={{ mb: 2 }}>
              Map questions to Course Outcomes to track attainment through assessment results.
            </Alert>
            <Grid container spacing={2}>
              {cos.map((co) => (
                <Grid item xs={12} sm={6} md={4} key={co.id}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" fontWeight={600}>{co.code}</Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {co.description}
                      </Typography>
                      <Chip label={`${co.mappingCount} questions mapped`} size="small" color="primary" variant="outlined" />
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </TabPanel>

          <TabPanel value={tabValue} index={4}>
            <Typography variant="h6" sx={{ mb: 2 }}>Attainment Dashboard</Typography>
            <AttainmentTable data={attainments} />
          </TabPanel>
        </CardContent>
      </Card>

      {/* CO Dialog */}
      <Dialog open={coDialogOpen} onClose={() => setCoDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>{editingCO ? 'Edit Course Outcome' : 'Create Course Outcome'}</DialogTitle>
        <DialogContent>
          <COForm
            initialData={editingCO}
            onSuccess={() => { setCoDialogOpen(false); setSuccess(editingCO ? 'CO updated' : 'CO created'); fetchData(); }}
            onCancel={() => setCoDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* PO Dialog */}
      <Dialog open={poDialogOpen} onClose={() => setPoDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Program Outcome</DialogTitle>
        <DialogContent sx={{ pt: '16px !important' }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField label="PO Code" value={poForm.code} onChange={(e) => setPoForm({ ...poForm, code: e.target.value })} fullWidth required />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField label="Name" value={poForm.name} onChange={(e) => setPoForm({ ...poForm, name: e.target.value })} fullWidth required />
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                label="Department"
                value={poForm.departmentId || ''}
                onChange={(e) => setPoForm({ ...poForm, departmentId: Number(e.target.value) })}
                fullWidth
                required
              >
                {departments.map((d) => (
                  <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField label="Description" value={poForm.description} onChange={(e) => setPoForm({ ...poForm, description: e.target.value })} fullWidth multiline rows={3} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPoDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreatePO}>Create</Button>
        </DialogActions>
      </Dialog>

      {/* PSO Dialog */}
      <Dialog open={psoDialogOpen} onClose={() => setPsoDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Program Specific Outcome</DialogTitle>
        <DialogContent sx={{ pt: '16px !important' }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField label="PSO Code" value={psoForm.code} onChange={(e) => setPsoForm({ ...psoForm, code: e.target.value })} fullWidth required />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField label="Name" value={psoForm.name} onChange={(e) => setPsoForm({ ...psoForm, name: e.target.value })} fullWidth required />
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                label="Department"
                value={psoForm.departmentId || ''}
                onChange={(e) => setPsoForm({ ...psoForm, departmentId: Number(e.target.value) })}
                fullWidth
                required
              >
                {departments.map((d) => (
                  <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField label="Description" value={psoForm.description} onChange={(e) => setPsoForm({ ...psoForm, description: e.target.value })} fullWidth multiline rows={3} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPsoDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreatePSO}>Create</Button>
        </DialogActions>
      </Dialog>

      {/* Mapping Dialog */}
      <Dialog open={mappingDialogOpen} onClose={() => setMappingDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Map Question to CO</DialogTitle>
        <DialogContent sx={{ pt: '16px !important' }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                select
                label="Course Outcome"
                value={mappingForm.coId || ''}
                onChange={(e) => setMappingForm({ ...mappingForm, coId: Number(e.target.value) })}
                fullWidth
                required
              >
                {cos.map((co) => (
                  <MenuItem key={co.id} value={co.id}>{co.code} - {co.description}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Question ID"
                type="number"
                value={mappingForm.questionId || ''}
                onChange={(e) => setMappingForm({ ...mappingForm, questionId: Number(e.target.value) })}
                fullWidth
                required
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setMappingDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleMapQuestion}>Map</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this {deleteTarget?.type}? This action cannot be undone.</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" color="error" onClick={handleDelete}>Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default OBEAssessmentPage;
