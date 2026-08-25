package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SyllabusUploadRequest;
import com.evalorithm.dto.response.SyllabusUploadResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.AIDifficulty;
import com.evalorithm.enums.BloomLevel;
import com.evalorithm.enums.ExamStatus;
import com.evalorithm.enums.ExamType;
import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.enums.QuestionStatus;
import com.evalorithm.enums.QuestionType;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.SyllabusUploadService;
import com.evalorithm.util.QuestionTemplates;
import com.evalorithm.util.QuestionTemplates.QuestionTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyllabusUploadServiceImpl implements SyllabusUploadService {

    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final DepartmentRepository departmentRepository;
    private final AIQuestionRepository aiQuestionRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamStudentRepository examStudentRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTemplates questionTemplates;
    private final ObjectMapper objectMapper;

    private static final String MODEL_VERSION = "evalorithm-syllabus-auto-v1";
    private static final String[] QUESTION_TYPES = {"MCQ", "TRUE_FALSE", "FILL_BLANKS"};
    private static final String[] DIFFICULTIES = {"EASY", "MEDIUM", "HARD"};

    @Override
    @Transactional
    public SyllabusUploadResponse uploadSyllabus(MultipartFile file, SyllabusUploadRequest request, Long userId) {
        return processSyllabus(file, request.getSubjectId(), request.getDepartmentId(), userId);
    }

    @Transactional
    public SyllabusUploadResponse processSyllabus(MultipartFile file, Long subjectId, Long departmentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        String text = extractText(file);
        List<SyllabusUploadResponse.TopicStructure> parsedTopics = parseSyllabusStructure(text);

        List<SyllabusUploadResponse.SavedUnit> savedUnits = new ArrayList<>();
        List<SyllabusUploadResponse.AIGeneratedQuestion> generatedQuestions = new ArrayList<>();

        Map<Integer, Unit> existingUnits = new HashMap<>();
        for (Unit u : unitRepository.findBySubjectId(subjectId)) {
            existingUnits.put(u.getNumber(), u);
        }
        Map<Long, Set<String>> unitTopicNames = new HashMap<>();

        for (SyllabusUploadResponse.TopicStructure topicStruct : parsedTopics) {
            int unitNum;
            try {
                unitNum = Integer.parseInt(topicStruct.getUnitNumber());
            } catch (NumberFormatException e) {
                unitNum = parsedTopics.indexOf(topicStruct) + 1;
            }

            Unit unit = existingUnits.get(unitNum);
            if (unit == null) {
                unit = Unit.builder()
                        .number(unitNum)
                        .name(topicStruct.getUnitName())
                        .subject(subject)
                        .build();
                unit = unitRepository.save(unit);
                existingUnits.put(unitNum, unit);
            } else if (isGenericUnitName(unit.getName()) && !isGenericUnitName(topicStruct.getUnitName())) {
                unit.setName(topicStruct.getUnitName());
                unit = unitRepository.save(unit);
            }

            final Long unitId = unit.getId();
            Set<String> existingNames = unitTopicNames.computeIfAbsent(unitId, k -> {
                Set<String> names = new HashSet<>();
                for (Topic t : topicRepository.findByUnitId(unitId)) {
                    names.add(t.getName().toLowerCase());
                }
                return names;
            });

            List<Long> topicIds = new ArrayList<>();
            List<Question> unitQuestions = new ArrayList<>();
            
            int numTopics = topicStruct.getTopics().size();
            int questionsPerTopic = numTopics > 0 ? Math.max(1, 10 / numTopics) : 0;

            for (String topicName : topicStruct.getTopics()) {
                String cleanTopic = topicName.replaceAll("^[-•·\\s]+", "").trim();
                if (cleanTopic.isEmpty() || cleanTopic.length() < 2) continue;
                if (cleanTopic.length() > 200) cleanTopic = cleanTopic.substring(0, 200).trim();
                
                Topic topic;
                if (!existingNames.contains(cleanTopic.toLowerCase())) {
                    existingNames.add(cleanTopic.toLowerCase());
                    topic = Topic.builder()
                            .name(cleanTopic)
                            .unit(unit)
                            .build();
                    topic = topicRepository.save(topic);
                } else {
                    final String finalCleanTopic = cleanTopic;
                    topic = topicRepository.findByUnitId(unitId).stream()
                            .filter(t -> t.getName().equalsIgnoreCase(finalCleanTopic))
                            .findFirst().orElse(null);
                }
                if (topic != null) {
                    topicIds.add(topic.getId());
                    List<Question> questions = generateQuestionsForTopic(
                            subject, department, unit, topic, user, questionsPerTopic);
                    unitQuestions.addAll(questions);
                    for (Question q : questions) {
                        generatedQuestions.add(SyllabusUploadResponse.AIGeneratedQuestion.builder()
                                .questionId(q.getId())
                                .questionText(q.getTitle())
                                .questionType(q.getQuestionType().name())
                                .difficulty(q.getDifficulty() != null ? q.getDifficulty().name() : "MEDIUM")
                                .bloomLevel(q.getBloomLevel() != null ? q.getBloomLevel().name() : "K2_UNDERSTAND")
                                .topicName(cleanTopic)
                                .unitName(topicStruct.getUnitName())
                                .build());
                    }
                }
            }

            savedUnits.add(SyllabusUploadResponse.SavedUnit.builder()
                    .unitId(unit.getId())
                    .unitName(unit.getName())
                    .unitNumber(unitNum)
                    .topicIds(topicIds)
                    .build());
                    
            if (!unitQuestions.isEmpty()) {
                createAutoExam(subject, department, unitQuestions, unitQuestions.size(), user, unitNum, unit.getName());
            }
        }

        String msg = String.format(
                "Syllabus processed: %d units saved, %d topics parsed, %d questions generated.",
                savedUnits.size(),
                savedUnits.stream().mapToInt(u -> u.getTopicIds().size()).sum(),
                generatedQuestions.size()
        );

        return SyllabusUploadResponse.builder()
                .extractedTopics(parsedTopics)
                .savedUnits(savedUnits)
                .generatedQuestions(generatedQuestions)
                .message(msg)
                .build();
    }

    private List<Question> generateQuestionsForTopic(Subject subject, Department department,
                                                      Unit unit, Topic topic, User user, int numQuestions) {
        List<Question> result = new ArrayList<>();
        log.info("Generating {} template questions for topic '{}' in unit '{}'", numQuestions, topic.getName(), unit.getName());
        
        List<QuestionTemplate> templates = questionTemplates.getTemplates("MCQ", "MEDIUM");
        
        for (int i = 0; i < Math.min(numQuestions, templates.size()); i++) {
            QuestionTemplate tmpl = templates.get(i);
            String qText = questionTemplates.fillPlaceholders(tmpl.getText(), subject.getName(), topic.getName(), unit.getName());
            String explanation = questionTemplates.fillPlaceholders(tmpl.getExplanation(), subject.getName(), topic.getName(), unit.getName());
            String[] options = tmpl.getOptions();
            int correctIdx = tmpl.getCorrectIndex();

            Question q = Question.builder()
                    .title(qText)
                    .description(qText)
                    .questionType(QuestionType.MCQ)
                    .difficulty(QuestionDifficulty.MEDIUM)
                    .bloomLevel(BloomLevel.K2_UNDERSTAND)
                    .marks(1)
                    .estimatedTime(2)
                    .explanation(explanation)
                    .subject(subject)
                    .unit(unit)
                    .topic(topic)
                    .department(department)
                    .status(QuestionStatus.APPROVED)
                    .createdBy(user)
                    .updatedBy(user)
                    .build();
            q = questionRepository.save(q);

            String[] labels = {"A", "B", "C", "D"};
            List<MCQOption> mcqOptions = new ArrayList<>();
            for (int j = 0; j < Math.min(options.length, 4); j++) {
                mcqOptions.add(MCQOption.builder()
                        .question(q)
                        .optionLabel(labels[j])
                        .optionText(options[j])
                        .isCorrect(j == correctIdx)
                        .build());
            }
            q.getMcqOptions().addAll(mcqOptions);
            q = questionRepository.save(q);

            result.add(q);
            log.info("Saved question {} for topic '{}'", i + 1, topic.getName());
        }
        
        log.info("Successfully generated {} questions for topic '{}'", result.size(), topic.getName());
        return result;
    }

    private SyllabusUploadResponse.CreatedExamInfo createAutoExam(Subject subject, Department department,
                                                                    List<Question> questions, int totalQCount,
                                                                    User user, int unitNum, String unitName) {
        String examTitle = subject.getName() + " - Unit " + unitNum + " Test";
        int totalMarks = totalQCount;
        int passingMarks = (int) Math.ceil(totalMarks * 0.4);

        Exam exam = Exam.builder()
                .title(examTitle)
                .description("Auto-generated unit test for " + unitName)
                .examType(ExamType.PRACTICE_TEST)
                .status(ExamStatus.PUBLISHED)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .durationMinutes(Math.max(30, totalQCount * 2))
                .totalMarks(totalMarks)
                .passingMarks(passingMarks)
                .maxAttempts(3)
                .negativeMarksEnabled(false)
                .negativeMarksValue(0.0)
                .randomizeQuestions(true)
                .randomizeOptions(true)
                .showResultsImmediately(true)
                .autoSubmit(true)
                .fullscreenRequired(true)
                .preventTabSwitch(true)
                .department(department)
                .subject(subject)
                .semester(subject.getSemester())
                .createdBy(user)
                .build();

        exam = examRepository.save(exam);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            ExamQuestion eq = ExamQuestion.builder()
                    .exam(exam)
                    .question(question)
                    .marks(1)
                    .orderNumber(i + 1)
                    .isActive(true)
                    .build();

            examQuestionRepository.save(eq);
        }

        List<StudentProfile> students = studentProfileRepository.findByDepartmentIdAndSemesterId(
                department.getId(), subject.getSemester().getId());
        
        for (StudentProfile sp : students) {
            ExamStudent es = ExamStudent.builder()
                    .exam(exam)
                    .studentProfile(sp)
                    .assignedAt(LocalDateTime.now())
                    .isEligible(true)
                    .build();
            examStudentRepository.save(es);
        }

        return SyllabusUploadResponse.CreatedExamInfo.builder()
                .examId(exam.getId())
                .examTitle(examTitle)
                .totalQuestions(totalQCount)
                .totalMarks(totalMarks)
                .examType("PRACTICE_TEST")
                .status("PUBLISHED")
                .build();
    }

    private String extractText(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null && isImageFile(fileName)) {
                return ocrImage(file);
            }
            Tika tika = new Tika();
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new BadRequestException("Failed to read file: " + e.getMessage());
        }
    }

    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".bmp") || lower.endsWith(".gif") || lower.endsWith(".tiff")
                || lower.endsWith(".tif") || lower.endsWith(".webp");
    }

    private String ocrImage(MultipartFile file) throws Exception {
        String tessDir = System.getenv().getOrDefault(
                "TESSERACT_HOME", "C:\\Program Files\\Tesseract-OCR");

        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setLanguage("eng");

        ParseContext context = new ParseContext();
        context.set(TesseractOCRConfig.class, config);

        TesseractOCRParser parser = new TesseractOCRParser();
        parser.setTesseractPath(tessDir);
        parser.setTessdataPath(tessDir + "\\tessdata");
        parser.setLanguage("eng");
        parser.initialize(new java.util.HashMap<>());

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        parser.parse(file.getInputStream(), handler, metadata, context);

        String text = handler.toString();
        if (text == null || text.isBlank()) {
            throw new BadRequestException(
                    "No text could be extracted from the image. Please upload a clearer photo of the syllabus.");
        }
        return text;
    }

    private List<SyllabusUploadResponse.TopicStructure> parseSyllabusStructure(String text) {
        List<SyllabusUploadResponse.TopicStructure> structures = new ArrayList<>();

        Pattern unitPattern = Pattern.compile("(?i)(unit\\s+[\\dIVXivx]+|module\\s+[\\dIVXivx]+|chapter\\s+[\\dIVXivx]+)[.:\\s]+(.+?)(?=unit\\s+[\\dIVXivx]+|module\\s+[\\dIVXivx]+|chapter\\s+[\\dIVXivx]+|$)", Pattern.DOTALL);
        Matcher unitMatcher = unitPattern.matcher(text);

        while (unitMatcher.find()) {
            String unitHeader = unitMatcher.group(1).trim();
            String unitContent = unitMatcher.group(2).trim();

            String unitNumber = unitHeader.replaceAll("(?i)(unit|module|chapter)\\s*", "").trim();

            List<String> topicList = new ArrayList<>();
            Pattern topicPattern = Pattern.compile("(?m)^\\s*\\d+[.)\\s]+(.+)");
            Matcher topicMatcher = topicPattern.matcher(unitContent);
            while (topicMatcher.find()) {
                topicList.add(topicMatcher.group(1).trim());
            }

            if (topicList.isEmpty()) {
                String[] parts = unitContent.split("[-.]");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty() && trimmed.length() > 3 && trimmed.length() < 100) {
                        topicList.add(trimmed);
                    }
                }
            }
            if (topicList.isEmpty()) {
                topicList.add("General concepts and overview");
            }

            SyllabusUploadResponse.TopicStructure structure = SyllabusUploadResponse.TopicStructure.builder()
                    .unitName(resolveUnitName(unitHeader, unitContent))
                    .unitNumber(unitNumber)
                    .topics(topicList)
                    .build();
            structures.add(structure);
        }

        if (structures.isEmpty() && !text.isBlank()) {
            SyllabusUploadResponse.TopicStructure defaultStructure = SyllabusUploadResponse.TopicStructure.builder()
                    .unitName("Extracted Content")
                    .unitNumber("1")
                    .topics(List.of(text.substring(0, Math.min(text.length(), 500))))
                    .build();
            structures.add(defaultStructure);
        }

        return structures;
    }

    private String resolveUnitName(String unitHeader, String unitContent) {
        String firstLine = unitContent.split("\\n")[0].trim();
        if (!firstLine.isEmpty()
                && firstLine.length() > 2
                && firstLine.length() <= 200
                && !firstLine.matches("^\\d+[.)\\s]+.*")
                && !firstLine.matches("(?i)^(unit|module|chapter)\\s+[\\dIVXivx]+.*")) {
            return firstLine.replaceFirst("[:\\s]+$", "");
        }
        return unitHeader.length() > 200 ? unitHeader.substring(0, 200) : unitHeader;
    }

    private boolean isGenericUnitName(String name) {
        if (name == null || name.isBlank()) return true;
        return name.matches("(?i)^(unit|module|chapter)\\s*[\\dIVXivx]*\\s*:?\\s*$");
    }
}
