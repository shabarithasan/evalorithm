package com.evalorithm.service.impl;

import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamNotificationServiceImpl implements ExamNotificationService {

    private final ExamNotificationRepository examNotificationRepository;
    private final ExamRepository examRepository;
    private final ExamStudentRepository examStudentRepository;

    @Override
    @Transactional
    public void sendExamPublishedNotification(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamStudent> students = examStudentRepository.findByExamId(examId);
        for (ExamStudent es : students) {
            User user = es.getStudentProfile().getUser();
            ExamNotification notification = ExamNotification.builder()
                    .exam(exam)
                    .user(user)
                    .title("Exam Published: " + exam.getTitle())
                    .message("A new exam '" + exam.getTitle() + "' has been published. Scheduled from " +
                            exam.getStartDate() + " to " + exam.getEndDate() + ". Duration: " +
                            exam.getDurationMinutes() + " minutes.")
                    .notificationType("EXAM_PUBLISHED")
                    .isRead(false)
                    .sentAt(LocalDateTime.now())
                    .build();
            examNotificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void sendExamReminder(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamStudent> students = examStudentRepository.findByExamId(examId);
        for (ExamStudent es : students) {
            User user = es.getStudentProfile().getUser();
            ExamNotification notification = ExamNotification.builder()
                    .exam(exam)
                    .user(user)
                    .title("Exam Reminder: " + exam.getTitle())
                    .message("Reminder: The exam '" + exam.getTitle() + "' is scheduled to start on " +
                            exam.getStartDate() + ". Please be prepared.")
                    .notificationType("EXAM_REMINDER")
                    .isRead(false)
                    .sentAt(LocalDateTime.now())
                    .build();
            examNotificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void sendResultPublishedNotification(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamStudent> students = examStudentRepository.findByExamId(examId);
        for (ExamStudent es : students) {
            User user = es.getStudentProfile().getUser();
            ExamNotification notification = ExamNotification.builder()
                    .exam(exam)
                    .user(user)
                    .title("Results Published: " + exam.getTitle())
                    .message("Results for the exam '" + exam.getTitle() + "' have been published. Check your results now.")
                    .notificationType("RESULT_PUBLISHED")
                    .isRead(false)
                    .sentAt(LocalDateTime.now())
                    .build();
            examNotificationRepository.save(notification);
        }
    }

    @Override
    public List<ExamNotification> getUserNotifications(Long userId) {
        return examNotificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return examNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        ExamNotification notification = examNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamNotification", "id", notificationId));
        notification.setIsRead(true);
        examNotificationRepository.save(notification);
    }
}
