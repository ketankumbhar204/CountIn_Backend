package com.countin.countin_backend.auth.application.service;

import com.countin.countin_backend.auth.api.dto.request.SendOtpRequest;
import com.countin.countin_backend.auth.api.dto.request.VerifyOtpRequest;
import com.countin.countin_backend.auth.api.dto.response.AuthTokenResponse;
import com.countin.countin_backend.auth.api.dto.response.SendOtpResponse;
import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.common.util.MobileNumberNormalizer;
import com.countin.countin_backend.config.security.JwtService;
import com.countin.countin_backend.config.security.UserPrincipal;
import com.countin.countin_backend.user.api.dto.response.UserResponse;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import com.countin.countin_backend.user.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SendOtpResponse sendOtp(SendOtpRequest request) {
        String mobileNumber = MobileNumberNormalizer.normalize(request.getMobileNumber());
        otpService.sendOtp(mobileNumber);

        return SendOtpResponse.builder()
                .mobileNumber(mobileNumber)
                .message("OTP sent successfully")
                .build();
    }

    @Transactional
    public AuthTokenResponse verifyOtp(VerifyOtpRequest request) {
        String mobileNumber = MobileNumberNormalizer.normalize(request.getMobileNumber());
        otpService.verifyOtp(mobileNumber, request.getOtp());

        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseGet(() -> createUser(mobileNumber));

        if (!user.isActive()) {
            throw new BusinessException("User account is inactive");
        }

        String token = jwtService.generateToken(user);

        return AuthTokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UserPrincipal principal = getAuthenticatedPrincipal();
        UserEntity user = userRepository.findByIdAndIsActiveTrue(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        return UserResponse.from(user);
    }

    private UserEntity createUser(String mobileNumber) {
        return userRepository.save(UserEntity.builder()
                .mobileNumber(mobileNumber)
                .fullName("User")
                .build());
    }

    private UserPrincipal getAuthenticatedPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        throw new BusinessException("Invalid authentication context");
    }
}
