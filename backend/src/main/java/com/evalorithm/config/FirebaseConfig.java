package com.evalorithm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // TODO: The user will need to provide their service account key file
        // For local development, this could be a path, but in production, we'd use env vars
        try {
            FileInputStream serviceAccount;
            try {
                // Try Render's Docker secret file path first
                serviceAccount = new FileInputStream("/etc/secrets/serviceAccountKey.json");
            } catch (Exception ex) {
                // Fallback to local path
                serviceAccount = new FileInputStream("serviceAccountKey.json");
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            // Log warning that Firebase is not configured, returning default
            // In a real scenario, you'd probably want this to fail if Firebase is strictly required
            System.err.println("WARNING: Firebase serviceAccountKey.json not found. Firebase Auth will fail.");
            return null; // Handle null gracefully in your filters
        }
    }
}
