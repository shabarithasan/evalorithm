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
  Chip,
  TextField,
  Grid,
  Alert,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import HistoryIcon from '@mui/icons-material/History';
import PeopleIcon from '@mui/icons-material/People';
import LockIcon from '@mui/icons-material/Lock';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { auditService } from '../../services';
import { AuditLog, LoginHistory } from '../../types';

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

const SecurityPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [auditLogs, setAuditLogs] = useState<AuditLog[]>([]);
  const [loginHistory, setLoginHistory] = useState<LoginHistory[]>([]);
  const [activeUsers, setActiveUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionFilter, setActionFilter] = useState('');
  const [userFilter, setUserFilter] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [logsRes, loginRes, usersRes] = await Promise.allSettled([
        auditService.getLogs(),
        auditService.getLoginHistory(),
        auditService.getActiveUsers(),
      ]);
      if (logsRes.status === 'fulfilled' && logsRes.value.success) {
        setAuditLogs(logsRes.value.data?.content || logsRes.value.data || []);
      }
      if (loginRes.status === 'fulfilled' && loginRes.value.success) {
        setLoginHistory(loginRes.value.data?.content || loginRes.value.data || []);
      }
      if (usersRes.status === 'fulfilled' && usersRes.value.success) {
        setActiveUsers(usersRes.value.data || []);
      }
    } catch {
      setError('Failed to load security data');
    } finally {
      setLoading(false);
    }
  };

  const filteredLogs = auditLogs.filter((log) => {
    if (actionFilter && !log.action.toLowerCase().includes(actionFilter.toLowerCase())) return false;
    if (userFilter && !log.userName.toLowerCase().includes(userFilter.toLowerCase())) return false;
    return true;
  });

  const getActionColor = (action: string): 'success' | 'error' | 'warning' | 'info' | 'default' => {
    switch (action) {
      case 'CREATE': return 'success';
      case 'DELETE': return 'error';
      case 'UPDATE': return 'warning';
      case 'LOGIN': return 'info';
      case 'LOGOUT': return 'info';
      default: return 'default';
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="Security" subtitle="Audit logs, login history, and security settings" />

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)} variant="scrollable" scrollButtons="auto">
            <Tab icon={<SecurityIcon />} label="Audit Logs" />
            <Tab icon={<HistoryIcon />} label="Login History" />
            <Tab icon={<PeopleIcon />} label="Active Users" />
            <Tab icon={<LockIcon />} label="Password Policy" />
          </Tabs>

          <TabPanel value={tabValue} index={0}>
            <Grid container spacing={2} sx={{ mb: 2 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Filter by Action"
                  value={actionFilter}
                  onChange={(e) => setActionFilter(e.target.value)}
                  fullWidth
                  size="small"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Filter by User"
                  value={userFilter}
                  onChange={(e) => setUserFilter(e.target.value)}
                  fullWidth
                  size="small"
                />
              </Grid>
            </Grid>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'primary.main' }}>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Timestamp</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>User</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Action</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Entity</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Description</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>IP Address</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredLogs.length === 0 ? (
                    <TableRow><TableCell colSpan={6} align="center"><Typography color="text.secondary" sx={{ py: 2 }}>No audit logs found</Typography></TableCell></TableRow>
                  ) : (
                    filteredLogs.map((log) => (
                      <TableRow key={log.id} hover>
                        <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
                        <TableCell>{log.userName}</TableCell>
                        <TableCell><Chip label={log.action} size="small" color={getActionColor(log.action)} /></TableCell>
                        <TableCell>{log.entityName} #{log.entityId}</TableCell>
                        <TableCell>{log.description}</TableCell>
                        <TableCell>{log.ipAddress}</TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'primary.main' }}>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>User</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Login Time</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Logout Time</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>IP Address</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Browser</TableCell>
                    <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loginHistory.length === 0 ? (
                    <TableRow><TableCell colSpan={6} align="center"><Typography color="text.secondary" sx={{ py: 2 }}>No login history</Typography></TableCell></TableRow>
                  ) : (
                    loginHistory.map((login) => (
                      <TableRow key={login.id} hover>
                        <TableCell>{login.userName}</TableCell>
                        <TableCell>{new Date(login.loginTime).toLocaleString()}</TableCell>
                        <TableCell>{login.logoutTime ? new Date(login.logoutTime).toLocaleString() : '-'}</TableCell>
                        <TableCell>{login.ipAddress}</TableCell>
                        <TableCell>{login.browser}</TableCell>
                        <TableCell>
                          <Chip label={login.isSuccessful ? 'Success' : 'Failed'} color={login.isSuccessful ? 'success' : 'error'} size="small" />
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
            <Typography variant="h6" sx={{ mb: 2 }}>Active Users ({activeUsers.length})</Typography>
            <List>
              {activeUsers.length === 0 ? (
                <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>No active users</Typography>
              ) : (
                activeUsers.map((user: any, index: number) => (
                  <React.Fragment key={index}>
                    <ListItem>
                      <ListItemIcon>
                        <Box
                          sx={{
                            width: 10,
                            height: 10,
                            borderRadius: '50%',
                            backgroundColor: 'success.main',
                          }}
                        />
                      </ListItemIcon>
                      <ListItemText
                        primary={user.name || user.userName || `User ${index + 1}`}
                        secondary={user.role || user.email || 'Online'}
                      />
                    </ListItem>
                    {index < activeUsers.length - 1 && <Divider />}
                  </React.Fragment>
                ))
              )}
            </List>
          </TabPanel>

          <TabPanel value={tabValue} index={3}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>Password Policy</Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Minimum Length" type="number" defaultValue={8} fullWidth size="small" />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Max Failed Attempts" type="number" defaultValue={5} fullWidth size="small" />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Lock Duration (minutes)" type="number" defaultValue={30} fullWidth size="small" />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Password Expiry (days)" type="number" defaultValue={90} fullWidth size="small" />
                  </Grid>
                </Grid>
                <Button variant="contained" sx={{ mt: 2 }}>Save Policy</Button>
              </CardContent>
            </Card>
          </TabPanel>
        </CardContent>
      </Card>
    </Box>
  );
};

export default SecurityPage;
