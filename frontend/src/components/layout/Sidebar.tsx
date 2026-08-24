import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Divider,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import BusinessIcon from '@mui/icons-material/Business';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import TopicIcon from '@mui/icons-material/Topic';
import PeopleIcon from '@mui/icons-material/People';
import SchoolIcon from '@mui/icons-material/School';
import SettingsIcon from '@mui/icons-material/Settings';
import AssignmentIcon from '@mui/icons-material/Assignment';
import QuizIcon from '@mui/icons-material/Quiz';
import CategoryIcon from '@mui/icons-material/Category';
import UploadIcon from '@mui/icons-material/Upload';
import DescriptionIcon from '@mui/icons-material/Description';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
import AssessmentIcon from '@mui/icons-material/Assessment';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import PsychologyIcon from '@mui/icons-material/Psychology';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import SummarizeIcon from '@mui/icons-material/Summarize';
import CardMembershipIcon from '@mui/icons-material/CardMembership';
import SecurityIcon from '@mui/icons-material/Security';
import BackupIcon from '@mui/icons-material/Backup';
import SettingsSuggestIcon from '@mui/icons-material/SettingsSuggest';
import RateReviewIcon from '@mui/icons-material/RateReview';
import SupportAgentIcon from '@mui/icons-material/SupportAgent';
import MonitorHeartIcon from '@mui/icons-material/MonitorHeart';
import FeedbackIcon from '@mui/icons-material/Feedback';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import { useAuth } from '../../hooks/useAuth';
import { DRAWER_WIDTH } from '../../utils/constants';

interface SidebarProps {
  mobileOpen: boolean;
  onMobileClose: () => void;
}

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactElement;
}

const adminNavItems: NavItem[] = [
  { label: 'Dashboard', path: '/admin/dashboard', icon: <DashboardIcon /> },
  { label: 'AI Dashboard', path: '/admin/ai-dashboard', icon: <SmartToyIcon /> },
  { label: 'AI Question Generator', path: '/admin/ai-questions', icon: <AutoAwesomeIcon /> },
  { label: 'Analytics', path: '/admin/analytics', icon: <AssessmentIcon /> },
  { label: 'Leaderboard', path: '/admin/leaderboard', icon: <EmojiEventsIcon /> },
  { label: 'Departments', path: '/admin/departments', icon: <BusinessIcon /> },
  { label: 'Semesters', path: '/admin/semesters', icon: <CalendarMonthIcon /> },
  { label: 'Subjects', path: '/admin/subjects', icon: <MenuBookIcon /> },
  { label: 'Units', path: '/admin/units', icon: <ViewModuleIcon /> },
  { label: 'Topics', path: '/admin/topics', icon: <TopicIcon /> },
  { label: 'Faculty', path: '/admin/faculty', icon: <PeopleIcon /> },
  { label: 'Students', path: '/admin/students', icon: <SchoolIcon /> },
  { label: 'Question Dashboard', path: '/admin/question-dashboard', icon: <DashboardCustomizeIcon /> },
  { label: 'Question Bank', path: '/admin/questions', icon: <QuizIcon /> },
  { label: 'Question Categories', path: '/admin/question-categories', icon: <CategoryIcon /> },
  { label: 'Bulk Import', path: '/admin/bulk-import', icon: <UploadIcon /> },
  { label: 'Syllabus Upload', path: '/admin/syllabus-upload', icon: <DescriptionIcon /> },
  { label: 'Exam Dashboard', path: '/admin/exam-dashboard', icon: <DashboardCustomizeIcon /> },
  { label: 'Exams', path: '/admin/exams', icon: <AssignmentIcon /> },
  { label: 'Exam Results', path: '/admin/exam-results', icon: <AssessmentIcon /> },
  { label: 'OBE Assessment', path: '/admin/obe', icon: <AccountBalanceIcon /> },
  { label: 'Attainment', path: '/admin/attainment', icon: <SummarizeIcon /> },
  { label: 'Certificates', path: '/admin/certificates', icon: <CardMembershipIcon /> },
  { label: 'Reports', path: '/admin/reports', icon: <SummarizeIcon /> },
  { label: 'Feedback', path: '/admin/feedback', icon: <RateReviewIcon /> },
  { label: 'Security', path: '/admin/security', icon: <SecurityIcon /> },
  { label: 'Backups', path: '/admin/backups', icon: <BackupIcon /> },
  { label: 'System Settings', path: '/admin/system-settings', icon: <SettingsSuggestIcon /> },
  { label: 'Settings', path: '/admin/settings', icon: <SettingsIcon /> },
  { label: 'Support', path: '/admin/support', icon: <SupportAgentIcon /> },
  { label: 'Monitoring', path: '/admin/monitoring', icon: <MonitorHeartIcon /> },
];

const facultyNavItems: NavItem[] = [
  { label: 'Dashboard', path: '/faculty/dashboard', icon: <DashboardIcon /> },
  { label: 'My Subjects', path: '/faculty/subjects', icon: <MenuBookIcon /> },
  { label: 'Exams', path: '/faculty/exams', icon: <AssignmentIcon /> },
  { label: 'Exam Results', path: '/faculty/exam-results', icon: <AssessmentIcon /> },
  { label: 'Analytics', path: '/faculty/analytics', icon: <AssessmentIcon /> },
  { label: 'Feedback', path: '/faculty/feedback', icon: <RateReviewIcon /> },
];

const studentNavItems: NavItem[] = [
  { label: 'Dashboard', path: '/student/dashboard', icon: <DashboardIcon /> },
  { label: 'My Analytics', path: '/student/analytics', icon: <AssessmentIcon /> },
  { label: 'Adaptive Test', path: '/student/adaptive-test', icon: <PsychologyIcon /> },
  { label: 'Adaptive Exam', path: '/student/adaptive-exam', icon: <SchoolIcon /> },
  { label: 'Recommendations', path: '/student/recommendations', icon: <LightbulbIcon /> },
  { label: 'Predictions', path: '/student/predictions', icon: <TrendingUpIcon /> },
  { label: 'Insights', path: '/student/insights', icon: <AutoAwesomeIcon /> },
  { label: 'My Subjects', path: '/student/subjects', icon: <MenuBookIcon /> },
  { label: 'My Exams', path: '/student/exams', icon: <AssignmentIcon /> },
  { label: 'Results', path: '/student/results', icon: <AssignmentIcon /> },
  { label: 'Feedback', path: '/student/feedback', icon: <FeedbackIcon /> },
  { label: 'Certificates', path: '/student/certificates', icon: <WorkspacePremiumIcon /> },
  { label: 'Help & Support', path: '/student/help', icon: <HelpOutlineIcon /> },
];

const Sidebar: React.FC<SidebarProps> = ({ mobileOpen, onMobileClose }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { isAdmin, isFaculty, isStudent } = useAuth();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const navItems = isAdmin ? adminNavItems : isFaculty ? facultyNavItems : studentNavItems;

  const handleNavClick = (path: string) => {
    navigate(path);
    if (isMobile) {
      onMobileClose();
    }
  };

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Toolbar sx={{ px: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box
            sx={{
              width: 36,
              height: 36,
              borderRadius: 2,
              background: 'linear-gradient(135deg, #1565C0, #42A5F5)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <Typography variant="h6" sx={{ color: '#fff', fontWeight: 700, fontSize: '1rem' }}>
              E
            </Typography>
          </Box>
          <Typography variant="h6" sx={{ fontWeight: 700, color: 'primary.main' }}>
            EVALORITHM
          </Typography>
        </Box>
      </Toolbar>
      <Divider />
      <Box sx={{ flex: 1, overflowY: 'auto', py: 1 }}>
        <List>
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.path} disablePadding sx={{ px: 1.5, py: 0.25 }}>
                <ListItemButton
                  onClick={() => handleNavClick(item.path)}
                  sx={{
                    borderRadius: 2,
                    backgroundColor: isActive ? 'primary.main' : 'transparent',
                    color: isActive ? '#fff' : 'text.primary',
                    '&:hover': {
                      backgroundColor: isActive ? 'primary.dark' : 'grey.100',
                    },
                    '& .MuiListItemIcon-root': {
                      color: isActive ? '#fff' : 'text.secondary',
                      minWidth: 40,
                    },
                    py: 1,
                  }}
                >
                  <ListItemIcon>{item.icon}</ListItemIcon>
                  <ListItemText
                    primary={item.label}
                    primaryTypographyProps={{
                      fontSize: '0.875rem',
                      fontWeight: isActive ? 600 : 500,
                    }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Box>
      <Divider />
      <Box sx={{ p: 2 }}>
        <Typography variant="caption" color="text.secondary" sx={{ textAlign: 'center', display: 'block' }}>
          v1.0.0
        </Typography>
      </Box>
    </Box>
  );

  return (
    <>
      {isMobile ? (
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={onMobileClose}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' },
          }}
        >
          {drawerContent}
        </Drawer>
      ) : (
        <Drawer
          variant="permanent"
          sx={{
            '& .MuiDrawer-paper': {
              width: DRAWER_WIDTH,
              boxSizing: 'border-box',
              borderRight: '1px solid',
              borderColor: 'grey.200',
            },
          }}
          open
        >
          {drawerContent}
        </Drawer>
      )}
    </>
  );
};

export default Sidebar;
