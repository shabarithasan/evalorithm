package com.evalorithm.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QuestionTemplates {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionTemplate {
        private String text;
        private String[] options;
        private int correctIndex;
        private String explanation;
        private String difficulty;
        private String bloomLevel;
    }

    private final Random random = new Random();

    public List<QuestionTemplate> getTemplates(String questionType, String difficulty) {
        return switch (questionType.toUpperCase()) {
            case "MCQ" -> getMCQTemplates(difficulty);
            case "TRUE_FALSE" -> getTrueFalseTemplates(difficulty);
            case "FILL_BLANKS" -> getFillBlanksTemplates(difficulty);
            case "ASSERTION_REASON" -> getAssertionReasonTemplates(difficulty);
            case "MATCH_FOLLOWING" -> getMatchFollowingTemplates(difficulty);
            case "PROGRAMMING" -> getProgrammingTemplates(difficulty);
            default -> getMCQTemplates(difficulty);
        };
    }

    public QuestionTemplate getRandomTemplate(String questionType, String difficulty) {
        List<QuestionTemplate> templates = getTemplates(questionType, difficulty);
        if (templates.isEmpty()) {
            templates = getTemplates(questionType, "MEDIUM");
        }
        if (templates.isEmpty()) {
            return getMCQTemplates("MEDIUM").get(0);
        }
        return templates.get(random.nextInt(templates.size()));
    }

    public String fillPlaceholders(String text, String subject, String topic, String unit) {
        String result = text;
        if (subject != null) result = result.replace("{subject}", subject);
        if (topic != null) result = result.replace("{topic}", topic);
        if (unit != null) result = result.replace("{unit}", unit);
        return result;
    }

    public String[] shuffleOptions(String[] options) {
        List<String> list = new ArrayList<>(Arrays.asList(options));
        Collections.shuffle(list);
        return list.toArray(new String[0]);
    }

    private List<QuestionTemplate> getMCQTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        if ("EASY".equals(diff) || "MEDIUM".equals(diff) || "HARD".equals(diff) || "EXPERT".equals(diff)) {
            templates.add(new QuestionTemplate(
                "In {subject}, which of the following best describes {topic}?",
                new String[]{"A basic concept", "An advanced algorithm", "A data structure", "A design pattern"},
                0, "This is a fundamental concept in {subject}.", diff, "K1_REMEMBER"
            ));
            templates.add(new QuestionTemplate(
                "What is the time complexity of the primary operation in {topic}?",
                new String[]{"O(1)", "O(n)", "O(n log n)", "O(n^2)"},
                1, "The primary operation in {topic} typically has linear time complexity.", diff, "K2_UNDERSTAND"
            ));
            templates.add(new QuestionTemplate(
                "Which data structure is most suitable for implementing {topic} in {subject}?",
                new String[]{"Array", "Linked List", "Hash Map", "Binary Tree"},
                2, "A Hash Map provides O(1) average lookup which is ideal for {topic}.", diff, "K3_APPLY"
            ));
            templates.add(new QuestionTemplate(
                "In {subject}, what is the primary advantage of using {topic}?",
                new String[]{"Reduced memory usage", "Faster access time", "Improved readability", "Better scalability"},
                1, "The main advantage of {topic} is improved access time.", diff, "K2_UNDERSTAND"
            ));
            templates.add(new QuestionTemplate(
                "Which of the following is NOT a property of {topic} in {subject}?",
                new String[]{"Encapsulation", "Polymorphism", "Redundancy", "Abstraction"},
                2, "Redundancy is generally avoided in well-designed systems.", diff, "K4_ANALYZE"
            ));
            templates.add(new QuestionTemplate(
                "Consider {topic} in {subject}. Which statement is true?",
                new String[]{"It always runs in O(1)", "It requires sorted data", "It can handle dynamic data", "It uses divide and conquer"},
                2, "{topic} can efficiently handle dynamic data sets.", diff, "K4_ANALYZE"
            ));
            templates.add(new QuestionTemplate(
                "What is the worst-case scenario for {topic}?",
                new String[]{"Best case performance", "Average case performance", "All elements are identical", "Input is already sorted"},
                2, "When all elements are identical, certain algorithms face worst-case scenarios.", diff, "K5_EVALUATE"
            ));
            templates.add(new QuestionTemplate(
                "In the context of {subject}, {topic} is most closely related to:",
                new String[]{"Memory management", "Process scheduling", "Data organization", "Network routing"},
                2, "{topic} primarily deals with how data is organized and stored.", diff, "K2_UNDERSTAND"
            ));
            templates.add(new QuestionTemplate(
                "Which principle of {subject} does {topic} primarily demonstrate?",
                new String[]{"Separation of concerns", "Single responsibility", "Divide and conquer", "All of the above"},
                3, "{topic} demonstrates multiple core principles of {subject}.", diff, "K5_EVALUATE"
            ));
            templates.add(new QuestionTemplate(
                "A student implemented {topic} but got incorrect results. The most likely error is:",
                new String[]{"Off-by-one error", "Missing base case", "Stack overflow", "Null pointer exception"},
                1, "Missing base cases are common errors when implementing {topic}.", diff, "K6_CREATE"
            ));
        }

        if ("HARD".equals(diff) || "EXPERT".equals(diff)) {
            templates.add(new QuestionTemplate(
                "In advanced {subject}, what trade-off does {topic} involve?",
                new String[]{"Space vs Time", "Readability vs Performance", "Security vs Usability", "Flexibility vs Complexity"},
                0, "Advanced implementations of {topic} often involve space-time trade-offs.", diff, "K5_EVALUATE"
            ));
            templates.add(new QuestionTemplate(
                "Which optimization technique would be most effective for {topic} in {subject}?",
                new String[]{"Memoization", "Loop unrolling", "Bit manipulation", "All of the above"},
                3, "Multiple optimization techniques can be applied to {topic}.", diff, "K6_CREATE"
            ));
        }

        return templates;
    }

    private List<QuestionTemplate> getTrueFalseTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        templates.add(new QuestionTemplate(
            "{topic} in {subject} is always implemented using arrays.",
            new String[]{"True", "False"},
            1, "{topic} can be implemented using various data structures, not just arrays.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "The time complexity of {topic} is O(n log n) in all cases.",
            new String[]{"True", "False"},
            1, "Time complexity varies based on input: best, average, and worst cases differ.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "{topic} is a fundamental concept in {subject}.",
            new String[]{"True", "False"},
            0, "{topic} is indeed a core concept studied in {subject}.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "In {subject}, {topic} cannot handle duplicate values.",
            new String[]{"True", "False"},
            1, "Most implementations of {topic} can handle duplicate values.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "Recursion is always more efficient than iteration for {topic}.",
            new String[]{"True", "False"},
            1, "Recursion often has overhead from function calls and may be less efficient.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "{topic} guarantees O(1) access time in all scenarios.",
            new String[]{"True", "False"},
            1, "Access time depends on the underlying implementation and may vary.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "The concept of {topic} was first introduced in the 1960s.",
            new String[]{"True", "False"},
            0, "Many foundational concepts in {subject} originated in the 1960s.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "Parallel processing can speed up {topic} operations significantly.",
            new String[]{"True", "False"},
            0, "Parallel processing can divide work across multiple processors.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Space complexity of {topic} is always O(n).",
            new String[]{"True", "False"},
            1, "Space complexity depends on the specific implementation variant.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Testing {topic} implementations requires both unit and integration tests.",
            new String[]{"True", "False"},
            0, "Comprehensive testing of {topic} requires multiple testing strategies.", diff, "K5_EVALUATE"
        ));

        return templates;
    }

    private List<QuestionTemplate> getFillBlanksTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        templates.add(new QuestionTemplate(
            "The time complexity of searching in a balanced binary search tree is ___.",
            new String[]{"O(1)", "O(n)", "O(log n)", "O(n log n)"},
            2, "Balanced BSTs maintain height of log n, giving O(log n) search.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "In {subject}, a ___ is used to resolve hash collisions.",
            new String[]{"Chaining", "Sorting", "Binary search", "Linear search"},
            0, "Chaining is the most common technique for handling hash collisions.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "The ___ property ensures database transactions are processed reliably.",
            new String[]{"ACID", "DAMP", "BASE", "CRUD"},
            0, "ACID properties guarantee reliable transaction processing.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "In {topic}, the ___ traversal visits nodes level by level.",
            new String[]{"Breadth-First", "Depth-First", "In-order", "Pre-order"},
            0, "Breadth-First traversal processes all nodes at the current depth first.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "The ___ algorithm is used to find the shortest path in a weighted graph.",
            new String[]{"Dijkstra's", "Bubble Sort", "Quick Sort", "Merge Sort"},
            0, "Dijkstra's algorithm efficiently finds shortest paths in weighted graphs.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "A ___ is a linear data structure that follows LIFO principle.",
            new String[]{"Stack", "Queue", "Tree", "Graph"},
            0, "Stack follows Last In First Out (LIFO) ordering.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "In {subject}, ___ is the process of converting code into machine language.",
            new String[]{"Compilation", "Debugging", "Testing", "Deployment"},
            0, "Compilation translates source code into executable machine code.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "The ___ complexity measures both time and space requirements of an algorithm.",
            new String[]{"Computational", "Cyclomatic", "Structural", "Functional"},
            0, "Computational complexity analyzes resource requirements of algorithms.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "___ is a technique to reduce the number of comparisons in a search algorithm.",
            new String[]{"Binary Search", "Linear Search", "Bubble Sort", "Selection Sort"},
            0, "Binary Search reduces comparisons by half each step using sorted data.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "In database normalization, ___ normal form eliminates transitive dependencies.",
            new String[]{"Third", "First", "Second", "Fourth"},
            0, "Third Normal Form (3NF) removes transitive dependencies.", diff, "K4_ANALYZE"
        ));

        return templates;
    }

    private List<QuestionTemplate> getAssertionReasonTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        templates.add(new QuestionTemplate(
            "Assertion: {topic} provides efficient data retrieval.\nReason: {topic} uses indexing internally.",
            new String[]{"Both true, reason explains assertion", "Both true, reason does not explain", "Assertion true, reason false", "Assertion false, reason true"},
            0, "Indexing in {topic} directly enables efficient retrieval.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Assertion: {subject} uses recursive algorithms frequently.\nReason: Recursion is always more efficient than iteration.",
            new String[]{"Both true, reason explains assertion", "Both true, reason does not explain", "Assertion true, reason false", "Assertion false, reason true"},
            2, "Recursion is not always more efficient; it has call stack overhead.", diff, "K5_EVALUATE"
        ));
        templates.add(new QuestionTemplate(
            "Assertion: {topic} can be implemented with arrays.\nReason: Arrays provide contiguous memory allocation.",
            new String[]{"Both true, reason explains assertion", "Both true, reason does not explain", "Assertion true, reason false", "Assertion false, reason true"},
            1, "Both are true but contiguous memory is not the reason arrays can implement {topic}.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Assertion: Hash tables provide O(1) average lookup.\nReason: Hash functions distribute keys uniformly.",
            new String[]{"Both true, reason explains assertion", "Both true, reason does not explain", "Assertion true, reason false", "Assertion false, reason true"},
            0, "Uniform distribution by hash functions is why hash tables achieve O(1) average lookup.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Assertion: {topic} in {subject} requires balanced trees.\nReason: Unbalanced trees always cause errors.",
            new String[]{"Both true, reason explains assertion", "Both true, reason does not explain", "Assertion true, reason false", "Assertion false, reason true"},
            2, "Unbalanced trees don't cause errors; they just degrade performance.", diff, "K5_EVALUATE"
        ));

        return templates;
    }

    private List<QuestionTemplate> getMatchFollowingTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        templates.add(new QuestionTemplate(
            "Match the following algorithms with their categories:\n1. Binary Search - A. Sorting\n2. Quick Sort - B. Searching\n3. BFS - C. Graph Traversal\n4. Merge Sort - D. Sorting",
            new String[]{"1-B, 2-A, 3-C, 4-D", "1-A, 2-B, 3-D, 4-C", "1-C, 2-D, 3-A, 4-B", "1-D, 2-C, 3-B, 4-A"},
            0, "Binary Search is searching, Quick Sort and Merge Sort are sorting, BFS is graph traversal.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "Match data structures with their properties:\n1. Array - A. Dynamic size\n2. Linked List - B. Fixed size, contiguous\n3. Stack - C. LIFO\n4. Queue - D. FIFO",
            new String[]{"1-B, 2-A, 3-C, 4-D", "1-A, 2-B, 3-D, 4-C", "1-C, 2-D, 3-A, 4-B", "1-D, 2-C, 3-B, 4-A"},
            0, "Array has fixed contiguous memory, Linked List is dynamic, Stack is LIFO, Queue is FIFO.", diff, "K1_REMEMBER"
        ));
        templates.add(new QuestionTemplate(
            "Match the following {subject} concepts:\n1. Normalization - A. Query Optimization\n2. Indexing - B. Reducing Redundancy\n3. Joins - C. Combining Tables\n4. Views - D. Virtual Tables",
            new String[]{"1-B, 2-A, 3-C, 4-D", "1-A, 2-B, 3-D, 4-C", "1-C, 2-D, 3-A, 4-B", "1-D, 2-C, 3-B, 4-A"},
            0, "Normalization reduces redundancy, Indexing optimizes queries, Joins combine tables, Views are virtual.", diff, "K2_UNDERSTAND"
        ));
        templates.add(new QuestionTemplate(
            "Match time complexities with operations:\n1. Access array element - A. O(n)\n2. Search unsorted array - B. O(1)\n3. Insert at end of array - C. O(log n)\n4. Binary search - D. O(n) worst",
            new String[]{"1-B, 2-A, 3-D, 4-C", "1-A, 2-B, 3-C, 4-D", "1-C, 2-D, 3-A, 4-B", "1-D, 2-C, 3-B, 4-A"},
            0, "Array access is O(1), linear search is O(n), insert may resize O(n), binary search is O(log n).", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Match {subject} design patterns:\n1. Singleton - A. Create objects flexibly\n2. Factory - B. One instance only\n3. Observer - C. Sequential access\n4. Iterator - D. Event notification",
            new String[]{"1-B, 2-A, 3-D, 4-C", "1-A, 2-B, 3-C, 4-D", "1-C, 2-D, 3-A, 4-B", "1-D, 2-C, 3-B, 4-A"},
            0, "Singleton ensures one instance, Factory creates objects, Observer handles events, Iterator enables sequential access.", diff, "K2_UNDERSTAND"
        ));

        return templates;
    }

    private List<QuestionTemplate> getProgrammingTemplates(String difficulty) {
        List<QuestionTemplate> templates = new ArrayList<>();
        String diff = difficulty != null ? difficulty : "MEDIUM";

        templates.add(new QuestionTemplate(
            "Write a function to find the maximum element in an array of integers. Analyze its time and space complexity.",
            new String[]{"O(n) time, O(1) space", "O(n log n) time, O(1) space", "O(n^2) time, O(n) space", "O(1) time, O(1) space"},
            0, "Finding max requires scanning all elements once: O(n) time, O(1) extra space.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Implement a function to reverse a linked list. What is the time complexity of your solution?",
            new String[]{"O(n) time", "O(n^2) time", "O(log n) time", "O(1) time"},
            0, "Reversing a linked list requires one pass through all nodes: O(n).", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Write a recursive function to compute the nth Fibonacci number. What is its time complexity?",
            new String[]{"O(2^n)", "O(n)", "O(n log n)", "O(n^2)"},
            0, "Naive recursion computes overlapping subproblems, leading to exponential time.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Design an algorithm to detect a cycle in a linked list. Which technique is most efficient?",
            new String[]{"Floyd's cycle detection", "Hashing all nodes", "Brute force comparison", "Binary search"},
            0, "Floyd's tortoise and hare algorithm detects cycles in O(n) time and O(1) space.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Write a function to check if a string is a palindrome. Consider edge cases.",
            new String[]{"Two pointer approach", "Stack-based approach", "Recursive approach", "All are valid"},
            3, "Multiple approaches work; two pointer is most space-efficient at O(1) space.", diff, "K3_APPLY"
        ));
        templates.add(new QuestionTemplate(
            "Implement a function to merge two sorted arrays. What optimization can you apply?",
            new String[]{"Two pointer technique", "Divide and conquer", "Hash map merging", "Bubble merge"},
            0, "Two pointer technique merges sorted arrays in O(n+m) time and O(n+m) space.", diff, "K4_ANALYZE"
        ));
        templates.add(new QuestionTemplate(
            "Write code to find all pairs in an array that sum to a given target value.",
            new String[]{"Hash map approach O(n)", "Nested loops O(n^2)", "Sorting + two pointers O(n log n)", "Both A and C are optimal"},
            3, "Hash map gives O(n) time; sort + two pointers gives O(n log n). Both are efficient.", diff, "K5_EVALUATE"
        ));

        return templates;
    }
}
