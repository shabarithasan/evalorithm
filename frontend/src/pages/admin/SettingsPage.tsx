import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Divider,
  Alert,
  CircularProgress,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { settingService } from '../../services';
import { Setting, SettingRequest } from '../../types';

const SettingsPage: React.FC = () => {
  const [settings, setSettings] = useState<Setting[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValues, setEditValues] = useState<{ value: string; description: string }>({
    value: '',
    description: '',
  });
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const response = await settingService.getAll();
      if (response.success) {
        setSettings(response.data);
      }
    } catch {
      setError('Failed to load settings');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (setting: Setting) => {
    setEditingId(setting.id);
    setEditValues({ value: setting.settingValue, description: setting.description });
  };

  const handleCancel = () => {
    setEditingId(null);
    setEditValues({ value: '', description: '' });
  };

  const handleSave = async (setting: Setting) => {
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      await settingService.update({
        settingKey: setting.settingKey,
        settingValue: editValues.value,
        description: editValues.description,
      });
      setEditingId(null);
      setSuccess('Setting updated successfully');
      fetchSettings();
    } catch {
      setError('Failed to update setting');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingScreen />;

  const appSettings = settings.filter(
    (s) => !s.settingKey.includes('SECURITY') && !s.settingKey.includes('MAIL')
  );
  const securitySettings = settings.filter(
    (s) => s.settingKey.includes('SECURITY') || s.settingKey.includes('MAIL')
  );

  const renderSetting = (setting: Setting) => {
    const isEditing = editingId === setting.id;
    return (
      <ListItem
        key={setting.id}
        secondaryAction={
          isEditing ? (
            <Box sx={{ display: 'flex', gap: 0.5 }}>
              <IconButton size="small" onClick={() => handleSave(setting)} disabled={saving}>
                {saving ? <CircularProgress size={18} /> : <SaveIcon fontSize="small" />}
              </IconButton>
              <IconButton size="small" onClick={handleCancel}>
                <CancelIcon fontSize="small" />
              </IconButton>
            </Box>
          ) : (
            <IconButton size="small" onClick={() => handleEdit(setting)}>
              <EditIcon fontSize="small" />
            </IconButton>
          )
        }
      >
        <ListItemText
          primary={setting.settingKey}
          secondary={
            isEditing ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mt: 1 }}>
                <TextField
                  size="small"
                  value={editValues.value}
                  onChange={(e) => setEditValues({ ...editValues, value: e.target.value })}
                  fullWidth
                />
                <TextField
                  size="small"
                  value={editValues.description}
                  onChange={(e) => setEditValues({ ...editValues, description: e.target.value })}
                  fullWidth
                />
              </Box>
            ) : (
              <>
                <Typography variant="body2" component="span" sx={{ fontWeight: 500 }}>
                  {setting.settingValue}
                </Typography>
                {setting.description && (
                  <Typography variant="caption" color="text.secondary" display="block">
                    {setting.description}
                  </Typography>
                )}
              </>
            )
          }
        />
      </ListItem>
    );
  };

  return (
    <Box>
      <PageHeader title="Settings" subtitle="Manage application settings" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 1 }}>
              Application Settings
            </Typography>
            <Divider sx={{ mb: 1 }} />
            {appSettings.length > 0 ? (
              <List>{appSettings.map(renderSetting)}</List>
            ) : (
              <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                No application settings found.
              </Typography>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 1 }}>
              Security & Mail Settings
            </Typography>
            <Divider sx={{ mb: 1 }} />
            {securitySettings.length > 0 ? (
              <List>{securitySettings.map(renderSetting)}</List>
            ) : (
              <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                No security settings found.
              </Typography>
            )}
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default SettingsPage;
