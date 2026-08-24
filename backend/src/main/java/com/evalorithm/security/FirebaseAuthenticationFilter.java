package com.evalorithm.security;

import com.evalorithm.entity.StudentProfile;
import com.evalorithm.entity.User;
import com.evalorithm.enums.Role;
import com.evalorithm.repository.StudentProfileRepository;
import com.evalorithm.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final com.evalorithm.repository.FacultyProfileRepository facultyProfileRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public FirebaseAuthenticationFilter(UserRepository userRepository, 
                                      StudentProfileRepository studentProfileRepository,
                                      com.evalorithm.repository.FacultyProfileRepository facultyProfileRepository,
                                      CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.facultyProfileRepository = facultyProfileRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(jwt);
                String email = decodedToken.getEmail();
                
                // If user doesn't exist in our DB, create them on the fly
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    String reqRole = request.getHeader("X-Register-Role");
                    Role role = Role.ROLE_STUDENT;
                    if ("ROLE_FACULTY".equals(reqRole)) {
                        role = Role.ROLE_FACULTY;
                    }
                    
                    String reqFirstName = request.getHeader("X-Register-FirstName");
                    String reqLastName = request.getHeader("X-Register-LastName");

                    User newUser = User.builder()
                            .email(email)
                            .password("FIREBASE_AUTH_DEFAULT") // Added dummy password for Firebase users
                            .firstName(StringUtils.hasText(reqFirstName) ? reqFirstName : (decodedToken.getName() != null ? decodedToken.getName().split(" ")[0] : "User"))
                            .lastName(StringUtils.hasText(reqLastName) ? reqLastName : (decodedToken.getName() != null && decodedToken.getName().split(" ").length > 1 ? decodedToken.getName().split(" ")[1] : ""))
                            .role(role)
                            .enabled(true)
                            .emailVerified(decodedToken.isEmailVerified())
                            .build();
                    newUser = userRepository.save(newUser);
                    
                    if (role == Role.ROLE_FACULTY) {
                        com.evalorithm.entity.FacultyProfile profile = com.evalorithm.entity.FacultyProfile.builder()
                                .user(newUser)
                                .facultyId("FAC-" + newUser.getId())
                                .designation("Assistant Professor")
                                .build();
                        facultyProfileRepository.save(profile);
                    } else {
                        StudentProfile profile = StudentProfile.builder()
                                .user(newUser)
                                .registerNumber("REG-" + newUser.getId())
                                .build();
                        studentProfileRepository.save(profile);
                    }
                }

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
