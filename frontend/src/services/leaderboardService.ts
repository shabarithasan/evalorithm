import api from './api';

const leaderboardService = {
  getStudentLeaderboard: () => api.get('/leaderboard/students'),
  getDepartmentLeaderboard: () => api.get('/leaderboard/departments'),
  getFacultyLeaderboard: () => api.get('/leaderboard/faculty'),
  getSubjectLeaderboard: () => api.get('/leaderboard/subjects'),
};

export default leaderboardService;
