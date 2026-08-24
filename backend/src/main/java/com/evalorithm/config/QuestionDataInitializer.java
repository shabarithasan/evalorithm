package com.evalorithm.config;

import com.evalorithm.entity.QuestionCategory;
import com.evalorithm.enums.Status;
import com.evalorithm.repository.QuestionCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionDataInitializer implements CommandLineRunner {

    private final QuestionCategoryRepository questionCategoryRepository;

    @Override
    public void run(String... args) {
        initializeCategories();
    }

    private void initializeCategories() {
        List<String> defaultCategories = List.of("General", "Conceptual", "Analytical", "Application", "Problem Solving");

        for (String categoryName : defaultCategories) {
            if (!questionCategoryRepository.existsByCategoryName(categoryName)) {
                QuestionCategory category = QuestionCategory.builder()
                        .categoryName(categoryName)
                        .description("Default question category: " + categoryName)
                        .status(Status.ACTIVE)
                        .build();
                questionCategoryRepository.save(category);
                log.info("Created default question category: {}", categoryName);
            }
        }
    }
}
