export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  role: Role;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type Role = 'ROLE_ADMIN' | 'ROLE_FACULTY' | 'ROLE_STUDENT';
export type Status = 'ACTIVE' | 'INACTIVE';
export type NotificationType = 'LOGIN' | 'SYSTEM' | 'ACADEMIC';

export interface User {
  id: number;
  userId?: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  profilePhotoUrl: string;
  role: Role;
  enabled: boolean;
  emailVerified: boolean;
  createdAt: string;
}

export interface Department {
  id: number;
  code: string;
  name: string;
  description: string;
  status: Status;
  createdAt: string;
}

export interface Semester {
  id: number;
  number: number;
  departmentId: number;
  departmentName: string;
  status: Status;
  createdAt: string;
}

export interface Subject {
  id: number;
  code: string;
  name: string;
  departmentId: number;
  departmentName: string;
  semesterId: number;
  semesterNumber: number;
  credits: number;
  description: string;
  status: Status;
  createdAt: string;
}

export interface Unit {
  id: number;
  number: number;
  name: string;
  subjectId: number;
  subjectName: string;
  description: string;
  createdAt: string;
}

export interface Topic {
  id: number;
  name: string;
  unitId: number;
  unitName: string;
  description: string;
  keywords: string;
  createdAt: string;
}

export interface Faculty {
  id: number;
  facultyId: string;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  departmentId: number;
  departmentName: string;
  designation: string;
  assignedSubjects: Subject[];
}

export interface Student {
  id: number;
  registerNumber: string;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  departmentId: number;
  departmentName: string;
  semesterId: number;
  semesterNumber: number;
  enrolledSubjects: Subject[];
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  read: boolean;
  createdAt: string;
}

export interface Setting {
  id: number;
  settingKey: string;
  settingValue: string;
  description: string;
}

export interface DashboardData {
  totalDepartments: number;
  totalSubjects: number;
  totalFaculty: number;
  totalStudents: number;
  totalQuestions: number;
}

export interface FacultyDashboardData {
  assignedSubjects: number;
  questionCount: number;
  pendingQuestions: number;
}

export interface StudentDashboardData {
  enrolledSubjects: number;
  upcomingExams: number;
  recentResults: any[];
}

export interface DepartmentRequest {
  code: string;
  name: string;
  description: string;
  status: Status;
}

export interface SemesterRequest {
  number: number;
  departmentId: number;
  status: Status;
}

export interface SubjectRequest {
  code: string;
  name: string;
  departmentId: number;
  semesterId: number;
  credits: number;
  description: string;
  status: Status;
}

export interface UnitRequest {
  number: number;
  name: string;
  subjectId: number;
  description: string;
}

export interface TopicRequest {
  name: string;
  unitId: number;
  description: string;
  keywords: string;
}

export interface FacultyRequest {
  facultyId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  departmentId: number;
  designation: string;
  assignedSubjectIds: number[];
}

export interface StudentRequest {
  registerNumber: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  departmentId: number;
  semesterId: number;
  enrolledSubjectIds: number[];
}

export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  phone: string;
  profilePhotoUrl: string;
}

export interface SettingRequest {
  settingKey: string;
  settingValue: string;
  description: string;
}

// Volume 2 - Question Repository Types

export type QuestionDifficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';
export type BloomLevel = 'K1_REMEMBER' | 'K2_UNDERSTAND' | 'K3_APPLY' | 'K4_ANALYZE' | 'K5_EVALUATE' | 'K6_CREATE';
export type QuestionType = 'MCQ' | 'TRUE_FALSE' | 'MATCH_FOLLOWING' | 'FILL_BLANKS' | 'ASSERTION_REASON' | 'DESCRIPTIVE' | 'CASE_STUDY' | 'PROGRAMMING';
export type QuestionStatus = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED';
export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type QuestionMediaType = 'IMAGE' | 'DIAGRAM' | 'CODE_SNIPPET' | 'PDF';

export interface QuestionCategory {
  id: number;
  categoryName: string;
  description: string;
  status: Status;
  questionCount: number;
  createdAt: string;
}

export interface MCQOption {
  id?: number;
  optionLabel: string;
  optionText: string;
  isCorrect: boolean;
  explanation: string;
}

export interface ProgrammingQuestionData {
  id?: number;
  problemStatement: string;
  inputFormat: string;
  outputFormat: string;
  constraints: string;
  sampleInput: string;
  sampleOutput: string;
  testCases: string;
  starterCode: string;
  solutionCode: string;
  programmingLanguage: string;
}

export interface CaseStudyData {
  id?: number;
  scenario: string;
  subQuestions: string;
}

export interface QuestionVersion {
  id: number;
  versionNumber: number;
  updatedByName: string;
  changeDescription: string;
  createdAt: string;
}

export interface QuestionApproval {
  id: number;
  approverName: string;
  status: ApprovalStatus;
  comments: string;
  approvedAt: string;
}

export interface QuestionStatistics {
  id: number;
  viewCount: number;
  usageCount: number;
  correctCount: number;
  wrongCount: number;
  correctPercentage: number;
  wrongPercentage: number;
  lastUsedAt: string;
}

export interface QuestionMedia {
  id: number;
  fileName: string;
  fileUrl: string;
  fileType: QuestionMediaType;
  fileSize: number;
  uploadedAt: string;
}

export interface Question {
  id: number;
  title: string;
  description: string;
  questionType: QuestionType;
  difficulty: QuestionDifficulty;
  bloomLevel: BloomLevel;
  marks: number;
  estimatedTime: number;
  explanation: string;
  reference: string;
  status: QuestionStatus;
  categoryId: number;
  categoryName: string;
  departmentId: number;
  departmentName: string;
  semesterId: number;
  semesterNumber: number;
  subjectId: number;
  subjectName: string;
  unitId: number;
  unitName: string;
  topicId: number;
  topicName: string;
  courseOutcome: string;
  programOutcome: string;
  programSpecificOutcome: string;
  createdByName: string;
  updatedByName: string;
  version: number;
  isArchived: boolean;
  mcqOptions: MCQOption[];
  programmingQuestion: ProgrammingQuestionData;
  caseStudy: CaseStudyData;
  statistics: QuestionStatistics;
  media: QuestionMedia[];
  createdAt: string;
  updatedAt: string;
}

export interface QuestionDashboardData {
  totalQuestions: number;
  approvedQuestions: number;
  pendingQuestions: number;
  rejectedQuestions: number;
  recentlyAdded: number;
}

export interface QuestionSearchFilter {
  departmentId?: number;
  semesterId?: number;
  subjectId?: number;
  unitId?: number;
  topicId?: number;
  questionType?: QuestionType;
  difficulty?: QuestionDifficulty;
  bloomLevel?: BloomLevel;
  status?: QuestionStatus;
  categoryId?: number;
  createdBy?: number;
  searchTerm?: string;
  isArchived?: boolean;
}

export interface QuestionRequest {
  title: string;
  description: string;
  questionType: QuestionType;
  difficulty: QuestionDifficulty;
  bloomLevel: BloomLevel;
  marks: number;
  estimatedTime: number;
  explanation: string;
  reference: string;
  categoryId: number;
  departmentId: number;
  semesterId: number;
  subjectId: number;
  unitId: number;
  topicId: number;
  courseOutcome: string;
  programOutcome: string;
  programSpecificOutcome: string;
  mcqOptions: MCQOption[];
  programmingQuestion: ProgrammingQuestionData;
  caseStudy: CaseStudyData;
  matchPairs?: { left: string; right: string }[];
  assertionReason?: { assertion: string; reason: string; correctOption: string };
}

export interface QuestionCategoryRequest {
  categoryName: string;
  description: string;
  status: Status;
}

export interface BulkImportResult {
  totalRows: number;
  successfulImports: number;
  failedImports: number;
  errors: string[];
}

export interface SyllabusExtractedTopic {
  unitNumber: number;
  unitName: string;
  topics: string[];
}

export interface SyllabusSavedUnit {
  unitId: number;
  unitName: string;
  unitNumber: number;
  topicIds: number[];
}

export interface SyllabusAIGeneratedQuestion {
  questionId: number;
  questionText: string;
  questionType: string;
  difficulty: string;
  bloomLevel: string;
  topicName: string;
  unitName: string;
}

export interface SyllabusCreatedExam {
  examId: number;
  examTitle: string;
  totalQuestions: number;
  totalMarks: number;
  examType: string;
  status: string;
}

export interface SyllabusUploadResult {
  extractedTopics: SyllabusExtractedTopic[];
  message: string;
  savedUnits?: SyllabusSavedUnit[];
  generatedQuestions?: SyllabusAIGeneratedQuestion[];
  createdExam?: SyllabusCreatedExam;
}

// Volume 3 - Assessment Engine Types

export type ExamType = 'UNIT_TEST' | 'SUBJECT_TEST' | 'SEMESTER_TEST' | 'INTERNAL_ASSESSMENT' | 'MOCK_TEST' | 'PRACTICE_TEST' | 'FINAL_EXAMINATION';
export type ExamStatus = 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED' | 'CANCELLED';
export type AttendanceStatus = 'ABSENT' | 'PRESENT' | 'LATE_JOIN' | 'COMPLETED' | 'LEFT_EARLY';
export type AnswerStatusType = 'ANSWERED' | 'NOT_ANSWERED' | 'MARKED_FOR_REVIEW' | 'ANSWERED_MARKED';
export type ExamNotificationType = 'EXAM_PUBLISHED' | 'EXAM_REMINDER' | 'EXAM_COMPLETED' | 'RESULT_PUBLISHED';

export interface Exam {
  id: number;
  title: string;
  description: string;
  examType: ExamType;
  status: ExamStatus;
  startDate: string;
  endDate: string;
  durationMinutes: number;
  totalMarks: number;
  passingMarks: number;
  maxAttempts: number;
  negativeMarksEnabled: boolean;
  negativeMarksValue: number;
  randomizeQuestions: boolean;
  randomizeOptions: boolean;
  showResultsImmediately: boolean;
  autoSubmit: boolean;
  fullscreenRequired: boolean;
  preventTabSwitch: boolean;
  departmentId: number;
  departmentName: string;
  semesterId: number;
  semesterNumber: number;
  subjectId: number;
  subjectName: string;
  createdByName: string;
  questionCount: number;
  studentCount: number;
  createdAt: string;
}

export interface ExamQuestion {
  id: number;
  questionId: number;
  questionTitle: string;
  questionType: string;
  questionDescription: string;
  difficulty: string;
  marks: number;
  orderNumber: number;
  isActive: boolean;
  options?: MCQOption[];
}

export interface LiveExamQuestion {
  examQuestionId: number;
  questionIndex: number;
  questionTitle: string;
  questionDescription: string;
  questionType: string;
  marks: number;
  orderNumber: number;
  options?: { optionLabel: string; optionText: string }[];
  totalQuestions: number;
  userAnswer?: StudentAnswer;
}

export interface StudentAnswer {
  examQuestionId: number;
  selectedOptionLabel?: string;
  selectedOptionIds?: string;
  textAnswer?: string;
  answerStatus: AnswerStatusType;
}

export interface ExamAttempt {
  id: number;
  examId: number;
  examTitle: string;
  studentName: string;
  startTime: string;
  endTime?: string;
  isActive: boolean;
  answerCount: number;
  totalQuestions: number;
}

export interface ExamResult {
  id: number;
  examTitle: string;
  studentName: string;
  registerNumber?: string;
  totalMarksObtained: number;
  totalMarksPossible: number;
  percentage: number;
  grade: string;
  isPassed: boolean;
  correctAnswers: number;
  wrongAnswers: number;
  skippedQuestions: number;
  timeTakenMinutes: number;
  evaluatedAt: string;
}

export interface ExamDashboardData {
  totalExams: number;
  activeExams: number;
  scheduledExams: number;
  completedExams: number;
  draftExams: number;
}

export interface ExamAttendance {
  id: number;
  studentName: string;
  registerNumber: string;
  status: AttendanceStatus;
  joinTime?: string;
  leaveTime?: string;
}

export interface ExamNotification {
  id: number;
  examId: number;
  examTitle: string;
  title: string;
  message: string;
  notificationType: ExamNotificationType;
  isRead: boolean;
  sentAt: string;
}

export interface ExamReport {
  examTitle: string;
  totalStudents: number;
  appeared: number;
  passed: number;
  failed: number;
  averageMarks: number;
  highestMarks: number;
  lowestMarks: number;
  passPercentage: number;
}

export interface ExamRequest {
  title: string;
  description: string;
  examType: ExamType;
  startDate: string;
  endDate: string;
  durationMinutes: number;
  totalMarks: number;
  passingMarks: number;
  maxAttempts: number;
  negativeMarksEnabled: boolean;
  negativeMarksValue: number;
  randomizeQuestions: boolean;
  randomizeOptions: boolean;
  showResultsImmediately: boolean;
  autoSubmit: boolean;
  fullscreenRequired: boolean;
  preventTabSwitch: boolean;
  departmentId: number;
  semesterId: number;
  subjectId: number;
  examQuestions: { questionId: number; marks: number; orderNumber: number }[];
  assignStudentIds: number[];
}

export interface SubmitExamResult {
  attemptId: number;
  totalAnswered: number;
  totalCorrect: number;
  totalWrong: number;
  totalSkipped: number;
  autoEvaluated: boolean;
  message: string;
}

// Volume 4 - AI, Adaptive Testing & Analytics Types

export type AIDifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';
export type LearningPriorityLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type RiskLevelType = 'LOW' | 'MEDIUM' | 'HIGH' | 'VERY_HIGH';
export type RecommendationTypeValue = 'WEAK_TOPIC' | 'REVISION' | 'PRACTICE_QUESTIONS' | 'MOCK_TEST' | 'STUDY_PLAN' | 'PRIORITY_TOPIC';
export type InsightTypeValue = 'BEST_SUBJECT' | 'WEAKEST_SUBJECT' | 'FREQUENTLY_WRONG' | 'IMPROVEMENT_TREND' | 'LEARNING_CURVE';

export interface AIQuestion {
  id: number;
  questionText: string;
  questionType: string;
  difficulty: string;
  bloomLevel: string;
  options?: { label: string; text: string; }[];
  correctAnswer: string;
  explanation: string;
  subjectName: string;
  unitName?: string;
  topicName?: string;
  isApproved: boolean;
  confidenceScore: number;
  createdAt: string;
}

export interface AIDashboardData {
  aiGeneratedQuestions: number;
  adaptiveExams: number;
  studentPerformance: number;
  weakTopicsCount: number;
  strongTopicsCount: number;
  recommendationsCount: number;
}

export interface AdaptiveSession {
  id: number;
  subjectName: string;
  currentDifficulty: string;
  questionsAnswered: number;
  correctAnswers: number;
  wrongAnswers: number;
  accuracy: number;
  streakCount: number;
  maxStreak: number;
  isActive: boolean;
  score: number;
}

export interface AdaptiveQuestion {
  questionId: number;
  questionText: string;
  questionType: string;
  difficulty: string;
  options?: { optionLabel: string; optionText: string; }[];
  marks: number;
  timeLimit: number;
}

export interface StudentAnalytics {
  studentId: number;
  studentName: string;
  subjectName: string;
  totalAttempted: number;
  correctAnswers: number;
  wrongAnswers: number;
  accuracy: number;
  averageScore: number;
  completionRate: number;
  avgTimePerQuestion: number;
  difficultyPerformance: Record<string, number>;
  unitPerformance: UnitPerformanceItem[];
  topicPerformance: TopicPerformanceItem[];
}

export interface SubjectPerformanceItem {
  subjectName: string;
  accuracy: number;
  totalQuestions: number;
  averageTime: number;
}

export interface UnitPerformanceItem {
  unitName: string;
  subjectName: string;
  accuracy: number;
  totalQuestions: number;
}

export interface TopicPerformanceItem {
  topicName: string;
  unitName: string;
  accuracy: number;
  totalQuestions: number;
}

export interface FacultyAnalyticsData {
  facultyId: number;
  facultyName: string;
  subjectName: string;
  subjectId?: number;
  totalExams: number;
  averageClassScore: number;
  totalStudents: number;
  passRate: number;
}

export interface AdminAnalyticsData {
  totalStudents: number;
  totalFaculty: number;
  totalSubjects: number;
  totalExams: number;
  overallPassRate: number;
  averageScore: number;
  studentGrowth: { month: string; count: number; }[];
  departmentPerformance: { departmentName: string; averageScore: number; passRate: number; }[];
  topPerformers: LeaderboardItem[];
  lowPerformers: LeaderboardItem[];
}

export interface LeaderboardItem {
  rank: number;
  studentId?: number;
  studentName?: string;
  departmentName?: string;
  score: number;
  accuracy: number;
  totalExams: number;
}

export interface Recommendation {
  id: number;
  type: RecommendationTypeValue;
  title: string;
  description: string;
  priority: LearningPriorityLevel;
  subjectName?: string;
  topicName?: string;
  unitName?: string;
  isRead: boolean;
  generatedAt: string;
}

export interface Prediction {
  id: number;
  subjectName: string;
  predictedMarks: number;
  predictedGrade: string;
  passProbability: number;
  riskLevel: RiskLevelType;
  suggestedImprovement: string;
  confidenceLevel: number;
  generatedAt: string;
}

export interface AIInsight {
  id: number;
  insightType: InsightTypeValue;
  title: string;
  description: string;
  subjectName?: string;
  value: number;
  generatedAt: string;
}

export interface SmartNotification {
  id: number;
  title: string;
  message: string;
  type: string;
  priority: LearningPriorityLevel;
  generatedAt: string;
}

export interface AccuracyTrend {
  date: string;
  accuracy: number;
  questionsAttempted: number;
}

export interface PerformanceHeatmap {
  subject: string;
  topic: string;
  accuracy: number;
  attempts: number;
}

export interface AIQuestionGenerateRequest {
  subjectId: number;
  unitId?: number;
  topicId?: number;
  departmentId: number;
  questionType: string;
  difficulty: string;
  bloomLevel?: string;
  count: number;
  additionalInstructions?: string;
}

// Volume 5 - OBE, Reports, Certificates, Security, Deployment Types

export type FeedbackTypeValue = 'STUDENT_FACULTY' | 'STUDENT_COURSE' | 'FACULTY_FEEDBACK' | 'GENERAL';
export type SupportTicketStatusValue = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type CertificateTypeValue = 'COMPLETION' | 'MERIT' | 'PARTICIPATION';
export type AuditActionValue = 'CREATE' | 'UPDATE' | 'DELETE' | 'LOGIN' | 'LOGOUT' | 'EXPORT' | 'IMPORT' | 'APPROVE' | 'REJECT' | 'BACKUP' | 'RESTORE';
export type ReportType = 'STUDENT' | 'FACULTY' | 'DEPARTMENT' | 'SEMESTER' | 'SUBJECT' | 'QUESTION_BANK' | 'EXAM' | 'ANALYTICS' | 'AI_PERFORMANCE';
export type ExportFormat = 'PDF' | 'EXCEL';

export interface CourseOutcome {
  id: number;
  code: string;
  description: string;
  subjectId: number;
  subjectName: string;
  departmentName: string;
  semesterNumber: number;
  bloomsLevel: string;
  isAttainable: boolean;
  mappingCount: number;
}

export interface ProgramOutcome {
  id: number;
  code: string;
  name: string;
  description: string;
  departmentName: string;
  mappingCount: number;
}

export interface ProgramSpecificOutcome {
  id: number;
  code: string;
  name: string;
  description: string;
  departmentName: string;
  mappingCount: number;
}

export interface Attainment {
  id: number;
  coCode: string;
  coDescription: string;
  subjectName: string;
  semesterNumber: number;
  academicYear: string;
  targetAttainment: number;
  actualAttainment: number;
  directAttainment: number;
  indirectAttainment: number;
  isAchieved: boolean;
}

export interface AttainmentDashboard {
  attainments: Attainment[];
  overallTarget: number;
  overallActual: number;
  percentageAchieved: number;
  departmentName: string;
}

export interface Certificate {
  id: number;
  certificateType: CertificateTypeValue;
  studentName: string;
  registerNumber: string;
  examTitle?: string;
  subjectName?: string;
  issuedDate: string;
  certificateNumber: string;
  qrCode: string;
  issuedByName: string;
  digitalSignature?: string;
}

export interface Feedback {
  id: number;
  feedbackType: FeedbackTypeValue;
  fromUserName: string;
  toUserName?: string;
  subjectName?: string;
  rating: number;
  comment: string;
  suggestions?: string;
  isAnonymous: boolean;
  createdAt: string;
}

export interface SupportTicket {
  id: number;
  subject: string;
  description: string;
  status: SupportTicketStatusValue;
  priority: string;
  assignedToName?: string;
  resolution?: string;
  createdAt: string;
  resolvedAt?: string;
}

export interface AuditLog {
  id: number;
  userName: string;
  action: AuditActionValue;
  entityName: string;
  entityId: number;
  description: string;
  ipAddress: string;
  timestamp: string;
}

export interface LoginHistory {
  id: number;
  userName: string;
  loginTime: string;
  logoutTime?: string;
  ipAddress: string;
  deviceInfo: string;
  browser: string;
  isSuccessful: boolean;
}

export interface SystemSetting {
  id: number;
  settingKey: string;
  settingValue: string;
  category: string;
  description: string;
  dataType: string;
  updatedByName?: string;
}

export interface Backup {
  id: number;
  fileName: string;
  fileSize: number;
  backupType: string;
  status: string;
  createdByName?: string;
  createdAt: string;
}

export interface AdminMonitoringData {
  onlineUsers: number;
  systemHealth: { cpuUsage: number; memoryUsage: number; diskUsage: number; };
  databaseHealth: { status: string; responseTime: number; };
  storageUsage: { total: number; used: number; };
}

export interface FAQ {
  id: number;
  question: string;
  answer: string;
  category: string;
}
