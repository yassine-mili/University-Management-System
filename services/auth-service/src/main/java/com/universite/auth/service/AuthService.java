package com.universite.auth.service;

import com.universite.auth.client.StudentServiceClient;
import com.universite.auth.dto.*;
import com.universite.auth.exception.InvalidTokenException;
import com.universite.auth.exception.UserAlreadyExistsException;
import com.universite.auth.exception.UserNotFoundException;
import com.universite.auth.model.RefreshToken;
import com.universite.auth.model.User;
import com.universite.auth.repository.RefreshTokenRepository;
import com.universite.auth.repository.UserRepository;
import com.universite.auth.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final StudentServiceClient studentServiceClient;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        user = userRepository.save(user);

        // Create or fetch student profile if role is STUDENT
        String studentId = null;
        if (user.getRole() == com.universite.auth.model.Role.STUDENT) {
            studentId = studentServiceClient.getOrCreateStudentProfile(user.getId(), user.getEmail(), user.getUsername());
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                accessTokenExpiration,
                mapToUserResponse(user, studentId)
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getUsername().trim();
        request.setUsername(identifier);

        // Allow login using either username or email for convenience
        if (identifier.contains("@")) {
            userRepository.findByEmail(identifier.toLowerCase())
                .ifPresent(user -> request.setUsername(user.getUsername()));
        }

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Revoke old refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        // Generate new tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                accessTokenExpiration,
                mapToUserResponse(user)
        );
    }

    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new AuthResponse(
                newAccessToken,
                requestRefreshToken,
                accessTokenExpiration,
                mapToUserResponse(user)
        );
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return mapToUserResponse(user);
    }

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private UserResponse mapToUserResponse(User user) {
        return mapToUserResponse(user, null);
    }

    private UserResponse mapToUserResponse(User user, String studentId) {
        String resolvedStudentId = studentId;

        if (resolvedStudentId == null && user.getRole() == com.universite.auth.model.Role.STUDENT) {
            resolvedStudentId = studentServiceClient.getOrCreateStudentProfile(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername()
            );
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .studentId(resolvedStudentId)
                .build();
    }
}
