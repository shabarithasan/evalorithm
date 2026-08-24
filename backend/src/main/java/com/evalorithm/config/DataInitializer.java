package com.evalorithm.config;

import com.evalorithm.entity.Department;
import com.evalorithm.entity.Semester;
import com.evalorithm.entity.User;
import com.evalorithm.enums.Role;
import com.evalorithm.enums.Status;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.SemesterRepository;
import com.evalorithm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        createDefaultDepartments();
        createDefaultSemesters();
    }

    private void createDefaultAdmin() {
        if (!userRepository.existsByEmail("admin@evalorithm.com")) {
            User admin = User.builder()
                    .email("admin@evalorithm.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@evalorithm.com");
        }
    }

    private void createDefaultDepartments() {
        if (departmentRepository.count() == 0) {
            Department cs = Department.builder()
                    .code("CS")
                    .name("Computer Science")
                    .description("Department of Computer Science and Engineering")
                    .status(Status.ACTIVE)
                    .build();
            departmentRepository.save(cs);

            Department ece = Department.builder()
                    .code("ECE")
                    .name("Electronics and Communication Engineering")
                    .description("Department of Electronics and Communication Engineering")
                    .status(Status.ACTIVE)
                    .build();
            departmentRepository.save(ece);

            Department me = Department.builder()
                    .code("ME")
                    .name("Mechanical Engineering")
                    .description("Department of Mechanical Engineering")
                    .status(Status.ACTIVE)
                    .build();
            departmentRepository.save(me);

            Department bca = Department.builder()
                    .code("BCA")
                    .name("Bachelor of Computer Applications")
                    .description("Department of Computer Applications")
                    .status(Status.ACTIVE)
                    .build();
            departmentRepository.save(bca);

            log.info("Default departments created: CS, ECE, ME, BCA");
        }
    }

    private void createDefaultSemesters() {
        if (semesterRepository.count() == 0) {
            departmentRepository.findAll().forEach(department -> {
                int totalSemesters = "BCA".equals(department.getCode()) ? 6 : 8;
                for (int i = 1; i <= totalSemesters; i++) {
                    Semester semester = Semester.builder()
                            .number(i)
                            .department(department)
                            .status(Status.ACTIVE)
                            .build();
                    semesterRepository.save(semester);
                }
            });
            log.info("Default semesters created for all departments (BCA: 6, others: 8)");
        }
    }
}
