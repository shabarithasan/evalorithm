import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import MainLayout from './components/layout/MainLayout';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';
import AdminDashboard from './pages/admin/AdminDashboard';
import DepartmentPage from './pages/admin/DepartmentPage';
import SemesterPage from './pages/admin/SemesterPage';
import SubjectPage from './pages/admin/SubjectPage';
import UnitPage from './pages/admin/UnitPage';
import TopicPage from './pages/admin/TopicPage';
import FacultyPage from './pages/admin/FacultyPage';
import StudentPage from './pages/admin/StudentPage';
import SettingsPage from './pages/admin/SettingsPage';
import QuestionBankPage from './pages/admin/QuestionBankPage';
import QuestionCreatePage from './pages/admin/QuestionCreatePage';
import QuestionEditPage from './pages/admin/QuestionEditPage';
import QuestionDetailPage from './pages/admin/QuestionDetailPage';
import QuestionCategoryPage from './pages/admin/QuestionCategoryPage';
import QuestionDashboard from './pages/admin/QuestionDashboard';
import BulkImportPage from './pages/admin/BulkImportPage';
import SyllabusUploadPage from './pages/admin/SyllabusUploadPage';
import FacultyDashboard from './pages/faculty/FacultyDashboard';
import FacultySubjectsPage from './pages/faculty/FacultySubjectsPage';
import FacultyExamPage from './pages/faculty/FacultyExamPage';
import FacultyExamResultsPage from './pages/faculty/FacultyExamResultsPage';
import StudentDashboard from './pages/student/StudentDashboard';
import StudentSubjectsPage from './pages/student/StudentSubjectsPage';
import ExamListPage from './pages/student/ExamListPage';
import ExamTakingPage from './pages/student/ExamTakingPage';
import ExamResultPage from './pages/student/ExamResultPage';
import ExamDashboard from './pages/admin/ExamDashboard';
import ExamManagementPage from './pages/admin/ExamManagementPage';
import ExamCreatePage from './pages/admin/ExamCreatePage';
import ExamEditPage from './pages/admin/ExamEditPage';
import ExamDetailPage from './pages/admin/ExamDetailPage';
import ExamResultsPage from './pages/admin/ExamResultsPage';
import ExamNotificationsPage from './pages/common/ExamNotificationsPage';
import ProfilePage from './pages/common/ProfilePage';
import NotificationsPage from './pages/common/NotificationsPage';
import NotFoundPage from './pages/common/NotFoundPage';
import LoadingScreen from './components/common/LoadingScreen';
import AIDashboard from './pages/admin/AIDashboard';
import AIQuestionGeneratorPage from './pages/admin/AIQuestionGeneratorPage';
import AdminAnalyticsPage from './pages/admin/AdminAnalyticsPage';
import LeaderboardPage from './pages/admin/LeaderboardPage';
import StudentAnalyticsPage from './pages/student/StudentAnalyticsPage';
import AdaptiveTestPage from './pages/student/AdaptiveTestPage';
import RecommendationsPage from './pages/student/RecommendationsPage';
import PredictionsPage from './pages/student/PredictionsPage';
import InsightsPage from './pages/student/InsightsPage';
import SmartNotificationsPage from './pages/student/SmartNotificationsPage';
import FacultyAnalyticsPage from './pages/faculty/FacultyAnalyticsPage';
import FacultySubjectAnalyticsPage from './pages/faculty/FacultySubjectAnalyticsPage';
import OBEAssessmentPage from './pages/admin/OBEAssessmentPage';
import AttainmentDashboard from './pages/admin/AttainmentDashboard';
import ReportsPage from './pages/admin/ReportsPage';
import CertificatesPage from './pages/admin/CertificatesPage';
import SecurityPage from './pages/admin/SecurityPage';
import BackupPage from './pages/admin/BackupPage';
import SystemSettingsPage from './pages/admin/SystemSettingsPage';
import FeedbackPage from './pages/admin/FeedbackPage';
import SupportPage from './pages/admin/SupportPage';
import AdminMonitoringPage from './pages/admin/AdminMonitoringPage';
import StudentFeedbackPage from './pages/student/StudentFeedbackPage';
import StudentCertificatesPage from './pages/student/StudentCertificatesPage';
import AdaptiveExamPage from './pages/student/AdaptiveExamPage';
import HelpPage from './pages/student/HelpPage';
import FacultyFeedbackPage from './pages/faculty/FacultyFeedbackPage';

const ProtectedRoute: React.FC<{ children: React.ReactNode; roles?: string[] }> = ({
  children,
  roles,
}) => {
  const { isAuthenticated, loading, user } = useAuth();

  if (loading) return <LoadingScreen />;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (roles && user && !roles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

const RoleRedirect: React.FC = () => {
  const { user, loading } = useAuth();

  if (loading) return <LoadingScreen />;
  if (!user) return <Navigate to="/login" replace />;

  switch (user.role) {
    case 'ROLE_ADMIN':
      return <Navigate to="/admin/dashboard" replace />;
    case 'ROLE_FACULTY':
      return <Navigate to="/faculty/dashboard" replace />;
    case 'ROLE_STUDENT':
      return <Navigate to="/student/dashboard" replace />;
    default:
      return <Navigate to="/login" replace />;
  }
};

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ThemeProvider>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />

            {/* Protected Routes */}
            <Route
              element={
                <ProtectedRoute>
                  <MainLayout />
                </ProtectedRoute>
              }
            >
              {/* Root redirect */}
              <Route path="/" element={<RoleRedirect />} />

              {/* Admin Routes */}
              <Route
                path="/admin/dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/departments"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <DepartmentPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/semesters"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SemesterPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/subjects"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SubjectPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/units"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <UnitPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/topics"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <TopicPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/faculty"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <FacultyPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/students"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <StudentPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/settings"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SettingsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/questions"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionBankPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/questions/create"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionCreatePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/questions/:id"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionDetailPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/questions/:id/edit"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionEditPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/question-categories"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionCategoryPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/question-dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <QuestionDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/bulk-import"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <BulkImportPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/syllabus-upload"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SyllabusUploadPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exam-dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamManagementPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams/create"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamCreatePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams/:id"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamDetailPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams/:id/edit"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamEditPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exam-results"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ExamResultsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/ai-dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AIDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/ai-questions"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AIQuestionGeneratorPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/analytics"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AdminAnalyticsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/leaderboard"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <LeaderboardPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/obe"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <OBEAssessmentPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/attainment"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AttainmentDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/reports"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <ReportsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/certificates"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <CertificatesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/security"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SecurityPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/backups"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <BackupPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/system-settings"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SystemSettingsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/feedback"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <FeedbackPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/support"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <SupportPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/monitoring"
                element={
                  <ProtectedRoute roles={['ROLE_ADMIN']}>
                    <AdminMonitoringPage />
                  </ProtectedRoute>
                }
              />

              {/* Faculty Routes */}
              <Route
                path="/faculty/dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultyDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/subjects"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultySubjectsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/exams"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultyExamPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/exam-results"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultyExamResultsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/analytics"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultyAnalyticsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/analytics/subject/:subjectId"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultySubjectAnalyticsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/faculty/feedback"
                element={
                  <ProtectedRoute roles={['ROLE_FACULTY']}>
                    <FacultyFeedbackPage />
                  </ProtectedRoute>
                }
              />

              {/* Student Routes */}
              <Route
                path="/student/dashboard"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <StudentDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/subjects"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <StudentSubjectsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/exams"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <ExamListPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/exams/:id/take"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <ExamTakingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/exams/:id/result"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <ExamResultPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/analytics"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <StudentAnalyticsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/adaptive-test"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <AdaptiveTestPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/adaptive-exam"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <AdaptiveExamPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/recommendations"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <RecommendationsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/predictions"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <PredictionsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/insights"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <InsightsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/feedback"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <StudentFeedbackPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/certificates"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <StudentCertificatesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/help"
                element={
                  <ProtectedRoute roles={['ROLE_STUDENT']}>
                    <HelpPage />
                  </ProtectedRoute>
                }
              />

              {/* Common Routes */}
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/notifications" element={<NotificationsPage />} />
            </Route>

            {/* 404 */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </ThemeProvider>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
