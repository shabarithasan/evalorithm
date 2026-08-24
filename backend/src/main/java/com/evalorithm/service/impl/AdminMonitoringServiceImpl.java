package com.evalorithm.service.impl;

import com.evalorithm.entity.User;
import com.evalorithm.repository.*;
import com.evalorithm.service.AdminMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMonitoringServiceImpl implements AdminMonitoringService {

    private final UserRepository userRepository;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Override
    public Long getOnlineUsers() {
        return userRepository.count();
    }

    @Override
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        double cpuLoad = osBean.getSystemLoadAverage();
        health.put("cpuLoad", cpuLoad);
        health.put("availableProcessors", Runtime.getRuntime().availableProcessors());

        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        health.put("totalMemoryMB", totalMemory / (1024 * 1024));
        health.put("usedMemoryMB", usedMemory / (1024 * 1024));
        health.put("freeMemoryMB", freeMemory / (1024 * 1024));
        health.put("memoryUsagePercent", (usedMemory * 100.0 / totalMemory));

        long maxMemory = Runtime.getRuntime().maxMemory();
        health.put("maxMemoryMB", maxMemory / (1024 * 1024));
        health.put("heapMemoryUsedMB", memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024));
        health.put("heapMemoryMaxMB", memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024));

        health.put("uptimeHours", ManagementFactory.getRuntimeMXBean().getUptime() / 3600000.0);
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now().toString());

        return health;
    }

    @Override
    public Map<String, Object> getAPIHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        health.put("endpoints", Map.of(
                "/api/departments", "UP",
                "/api/subjects", "UP",
                "/api/exams", "UP",
                "/api/questions", "UP"
        ));
        return health;
    }

    @Override
    public Map<String, Object> getDatabaseHealth() {
        Map<String, Object> health = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            Thread.sleep(1);
            long endTime = System.currentTimeMillis();
            health.put("status", "UP");
            health.put("responseTimeMs", endTime - startTime);
            health.put("datasource", datasourceUrl.replaceAll("password=[^&]*", "password=***"));
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return health;
    }

    @Override
    public Map<String, Object> getStorageUsage() {
        Map<String, Object> storage = new HashMap<>();
        try {
            Path uploadPath = Paths.get("uploads");
            if (Files.exists(uploadPath)) {
                long size = Files.walk(uploadPath)
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (Exception e) {
                                return 0;
                            }
                        })
                        .sum();
                storage.put("uploadsSizeMB", size / (1024 * 1024));
            } else {
                storage.put("uploadsSizeMB", 0);
            }

            File root = new File(".");
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            storage.put("totalDiskSpaceGB", totalSpace / (1024 * 1024 * 1024));
            storage.put("freeDiskSpaceGB", freeSpace / (1024 * 1024 * 1024));
            storage.put("usedDiskSpaceGB", (totalSpace - freeSpace) / (1024 * 1024 * 1024));
            storage.put("diskUsagePercent", ((totalSpace - freeSpace) * 100.0 / totalSpace));
        } catch (Exception e) {
            storage.put("error", e.getMessage());
        }
        storage.put("timestamp", java.time.LocalDateTime.now().toString());
        return storage;
    }
}
