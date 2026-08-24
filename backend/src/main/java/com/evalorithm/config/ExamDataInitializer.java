package com.evalorithm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamDataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Volume 3 Assessment Engine module loaded. No seed data required.");
    }
}
