import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Tabs,
  Tab,
  TextField,
  Grid,
  Button,
  Alert,
  Switch,
  FormControlLabel,
  Divider,
  CircularProgress,
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { systemSettingService } from '../../services';
import { SystemSetting } from '../../types';

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

const categories = ['ACADEMIC', 'INSTITUTION', 'DEPARTMENT', 'EMAIL', 'NOTIFICATION', 'AI', 'SECURITY'];

const SystemSettingsPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [settings, setSettings] = useState<SystemSetting[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editValues, setEditValues] = useState<Record<number, string>>({});

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const response = await systemSettingService.getAll();
      if (response.success) {
        const data = response.data?.content || response.data || [];
        setSettings(data);
        const values: Record<number, string> = {};
        data.forEach((s: SystemSetting) => { values[s.id] = s.settingValue; });
        setEditValues(values);
      }
    } catch {
      setError('Failed to load settings');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (setting: SystemSetting) => {
    setSaving(true);
    setError('');
    try {
      await systemSettingService.update(setting.id, {
        settingValue: editValues[setting.id] || setting.settingValue,
      });
      setSuccess(`Setting "${setting.settingKey}" updated successfully`);
      fetchSettings();
    } catch {
      setError('Failed to update setting');
    } finally {
      setSaving(false);
    }
  };

  const getSettingsByCategory = (category: string) =>
    settings.filter((s) => s.category === category);

  const renderSettingsTab = (category: string) => {
    const categorySettings = getSettingsByCategory(category);
    return (
      <Box>
        {categorySettings.length === 0 ? (
          <Typography color="text.secondary" sx={{ py: 2 }}>No settings in this category.</Typography>
        ) : (
          <Grid container spacing={2}>
            {categorySettings.map((setting) => (
              <Grid item xs={12} sm={6} key={setting.id}>
                <Card variant="outlined">
                  <CardContent>
                    <Typography variant="subtitle2" fontWeight={600}>{setting.settingKey}</Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
                      {setting.description}
                    </Typography>
                    {setting.dataType === 'BOOLEAN' ? (
                      <FormControlLabel
                        control={
                          <Switch
                            checked={editValues[setting.id] === 'true'}
                            onChange={(e) => setEditValues({ ...editValues, [setting.id]: e.target.checked ? 'true' : 'false' })}
                          />
                        }
                        label={editValues[setting.id] === 'true' ? 'Enabled' : 'Disabled'}
                      />
                    ) : (
                      <TextField
                        size="small"
                        value={editValues[setting.id] || ''}
                        onChange={(e) => setEditValues({ ...editValues, [setting.id]: e.target.value })}
                        fullWidth
                        type={setting.dataType === 'NUMBER' ? 'number' : 'text'}
                      />
                    )}
                    <Button
                      size="small"
                      startIcon={<SaveIcon />}
                      onClick={() => handleSave(setting)}
                      disabled={saving}
                      sx={{ mt: 1 }}
                    >
                      Save
                    </Button>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    );
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="System Settings" subtitle="Configure system-wide settings" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs
            value={tabValue}
            onChange={(_, v) => setTabValue(v)}
            variant="scrollable"
            scrollButtons="auto"
          >
            {categories.map((cat, i) => (
              <Tab key={cat} label={cat.replace('_', ' ')} />
            ))}
          </Tabs>
          {categories.map((cat, i) => (
            <TabPanel key={cat} value={tabValue} index={i}>
              {renderSettingsTab(cat)}
            </TabPanel>
          ))}
        </CardContent>
      </Card>
    </Box>
  );
};

export default SystemSettingsPage;
