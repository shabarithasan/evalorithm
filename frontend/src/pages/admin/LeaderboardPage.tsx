import React, { useState, useEffect } from 'react';
import { Box, Typography, Tabs, Tab, CircularProgress } from '@mui/material';
import LeaderboardTable from '../../components/analytics/LeaderboardTable';
import LoadingScreen from '../../components/common/LoadingScreen';
import { leaderboardService } from '../../services';
import { LeaderboardItem } from '../../types';

const LeaderboardPage: React.FC = () => {
  const [tab, setTab] = useState(0);
  const [studentData, setStudentData] = useState<LeaderboardItem[]>([]);
  const [departmentData, setDepartmentData] = useState<LeaderboardItem[]>([]);
  const [facultyData, setFacultyData] = useState<LeaderboardItem[]>([]);
  const [subjectData, setSubjectData] = useState<LeaderboardItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    try {
      const [s, d, f, sub] = await Promise.allSettled([
        leaderboardService.getStudentLeaderboard(),
        leaderboardService.getDepartmentLeaderboard(),
        leaderboardService.getFacultyLeaderboard(),
        leaderboardService.getSubjectLeaderboard(),
      ]);

      if (s.status === 'fulfilled' && s.value.data?.success) setStudentData(s.value.data.data || []);
      if (d.status === 'fulfilled' && d.value.data?.success) setDepartmentData(d.value.data.data || []);
      if (f.status === 'fulfilled' && f.value.data?.success) setFacultyData(f.value.data.data || []);
      if (sub.status === 'fulfilled' && sub.value.data?.success) setSubjectData(sub.value.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load leaderboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  const tabLabels = ['Student Leaderboard', 'Department Leaderboard', 'Faculty Leaderboard', 'Subject Leaderboard'];
  const tabData = [studentData, departmentData, facultyData, subjectData];
  const tabTitles = ['Student Rankings', 'Department Rankings', 'Faculty Rankings', 'Subject Rankings'];

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Leaderboard</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3 }}>
        {tabLabels.map((label) => (
          <Tab key={label} label={label} />
        ))}
      </Tabs>
      <LeaderboardTable data={tabData[tab]} title={tabTitles[tab]} />
    </Box>
  );
};

export default LeaderboardPage;
