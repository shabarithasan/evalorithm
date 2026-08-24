package com.evalorithm.config;

import com.evalorithm.entity.*;
import com.evalorithm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Volume5DataInitializer implements CommandLineRunner {

    private final CourseOutcomeRepository courseOutcomeRepository;
    private final ProgramOutcomeRepository programOutcomeRepository;
    private final ProgramSpecificOutcomeRepository psoRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public void run(String... args) {
        log.info("Volume 5: Initializing OBE Assessment, Reports, Certificates, Security, Feedback, Help, and System Settings data...");

        initializeDefaultSystemSettings();
        initializeProgramOutcomes();
        initializeProgramSpecificOutcomes();
        initializeCourseOutcomes();

        log.info("Volume 5: Data initialization completed.");
    }

    private void initializeDefaultSystemSettings() {
        createSettingIfNotExists("institution.name", "Evalorithm University", "INSTITUTION", "Institution name", "STRING");
        createSettingIfNotExists("institution.code", "EVLU", "INSTITUTION", "Institution code", "STRING");
        createSettingIfNotExists("institution.email", "admin@evalorithm.edu", "INSTITUTION", "Institution email", "STRING");
        createSettingIfNotExists("institution.phone", "+91-9876543210", "INSTITUTION", "Institution phone", "STRING");

        createSettingIfNotExists("academic.default_attainment_target", "60", "ACADEMIC", "Default CO attainment target percentage", "INTEGER");
        createSettingIfNotExists("academic.passing_percentage", "40", "ACADEMIC", "Minimum passing percentage", "INTEGER");
        createSettingIfNotExists("academic.grading_scale", "{\"A+\":\"90-100\",\"A\":\"80-89\",\"B+\":\"70-79\",\"B\":\"60-69\",\"C\":\"50-59\",\"F\":\"0-49\"}", "ACADEMIC", "Grading scale JSON", "JSON");

        createSettingIfNotExists("email.smtp_host", "smtp.gmail.com", "EMAIL", "SMTP host", "STRING");
        createSettingIfNotExists("email.smtp_port", "587", "EMAIL", "SMTP port", "INTEGER");
        createSettingIfNotExists("email.enabled", "true", "EMAIL", "Email notifications enabled", "BOOLEAN");

        createSettingIfNotExists("notification.exam_reminder", "true", "NOTIFICATION", "Enable exam reminder notifications", "BOOLEAN");
        createSettingIfNotExists("notification.result_published", "true", "NOTIFICATION", "Enable result published notifications", "BOOLEAN");

        createSettingIfNotExists("ai.question_generation_enabled", "true", "AI", "Enable AI question generation", "BOOLEAN");
        createSettingIfNotExists("ai.difficulty_adjustment", "true", "AI", "Enable adaptive difficulty adjustment", "BOOLEAN");

        createSettingIfNotExists("security.session_timeout", "3600", "SECURITY", "Session timeout in seconds", "INTEGER");
        createSettingIfNotExists("security.max_login_attempts", "5", "SECURITY", "Max failed login attempts before lockout", "INTEGER");
        createSettingIfNotExists("security.password_min_length", "8", "SECURITY", "Minimum password length", "INTEGER");
        createSettingIfNotExists("security.require_2fa", "false", "SECURITY", "Require two-factor authentication", "BOOLEAN");

        log.info("Default system settings initialized.");
    }

    private void initializeProgramOutcomes() {
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            if (programOutcomeRepository.findByDepartmentId(dept.getId()).isEmpty()) {
                String[] poNames = {
                    "Engineering Knowledge", "Problem Analysis", "Design and Development of Solutions",
                    "Conduct Investigations of Complex Problems", "Modern Tool Usage", "Engineer and Society",
                    "Environment and Sustainability", "Ethics", "Individual and Team Work",
                    "Communication", "Project Management and Finance", "Life-long Learning"
                };
                for (int i = 0; i < poNames.length; i++) {
                    ProgramOutcome po = ProgramOutcome.builder()
                            .code("PO" + (i + 1))
                            .name(poNames[i])
                            .description("Program Outcome: " + poNames[i])
                            .department(dept)
                            .build();
                    programOutcomeRepository.save(po);
                }
                log.info("Created 12 POs for department: {}", dept.getName());
            }
        }
    }

    private void initializeProgramSpecificOutcomes() {
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            if (psoRepository.findByDepartmentId(dept.getId()).isEmpty()) {
                for (int i = 1; i <= 3; i++) {
                    ProgramSpecificOutcome pso = ProgramSpecificOutcome.builder()
                            .code("PSO" + i)
                            .name("Program Specific Outcome " + i)
                            .description("PSO " + i + " for " + dept.getName())
                            .department(dept)
                            .build();
                    psoRepository.save(pso);
                }
                log.info("Created 3 PSOs for department: {}", dept.getName());
            }
        }
    }

    private void initializeCourseOutcomes() {
        List<Subject> subjects = subjectRepository.findAll();
        String[] bloomsLevels = {"Remember", "Understand", "Apply", "Analyze", "Evaluate", "Create"};

        for (Subject subject : subjects) {
            if (courseOutcomeRepository.findBySubjectId(subject.getId()).isEmpty()) {
                for (int i = 1; i <= 6; i++) {
                    CourseOutcome co = CourseOutcome.builder()
                            .code("CO" + i)
                            .description("Course Outcome " + i + " for " + subject.getName())
                            .subject(subject)
                            .department(subject.getDepartment())
                            .semester(subject.getSemester())
                            .bloomsLevel(bloomsLevels[Math.min(i - 1, bloomsLevels.length - 1)])
                            .isAttainable(true)
                            .build();
                    courseOutcomeRepository.save(co);
                }
                log.info("Created 6 COs for subject: {}", subject.getName());
            }
        }
    }

    private void createSettingIfNotExists(String key, String value, String category, String description, String dataType) {
        if (systemSettingRepository.findBySettingKey(key).isEmpty()) {
            SystemSetting setting = SystemSetting.builder()
                    .settingKey(key)
                    .settingValue(value)
                    .category(category)
                    .description(description)
                    .dataType(dataType)
                    .isEncrypted(false)
                    .build();
            systemSettingRepository.save(setting);
        }
    }
}
