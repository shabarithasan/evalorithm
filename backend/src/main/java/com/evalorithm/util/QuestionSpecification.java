package com.evalorithm.util;

import com.evalorithm.dto.request.QuestionSearchRequest;
import com.evalorithm.entity.Question;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class QuestionSpecification {

    public static Specification<Question> withFilters(QuestionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getDepartmentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("id"), request.getDepartmentId()));
            }

            if (request.getSemesterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("semester").get("id"), request.getSemesterId()));
            }

            if (request.getSubjectId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("subject").get("id"), request.getSubjectId()));
            }

            if (request.getUnitId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("unit").get("id"), request.getUnitId()));
            }

            if (request.getTopicId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("topic").get("id"), request.getTopicId()));
            }

            if (request.getQuestionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("questionType"), request.getQuestionType()));
            }

            if (request.getDifficulty() != null) {
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), request.getDifficulty()));
            }

            if (request.getBloomLevel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("bloomLevel"), request.getBloomLevel()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getCreatedBy() != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdBy").get("id"), request.getCreatedBy()));
            }

            if (request.getSearchTerm() != null && !request.getSearchTerm().isEmpty()) {
                String searchTerm = "%" + request.getSearchTerm().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm);
                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
