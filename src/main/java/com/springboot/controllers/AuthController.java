package com.springboot.controllers;

import com.springboot.entities.Otp;
import com.springboot.payload.request.LoginRequest;
import com.springboot.payload.request.SignupRequest;
import com.springboot.payload.request.VerifyOtpRequest;
import com.springboot.payload.response.JwtResponse;
import com.springboot.payload.response.MessageResponse;
import com.springboot.payload.response.OtpResponse;
import com.springboot.security.jwt.JwtUtils;
import com.springboot.security.services.UserDetailsImpl;
import com.springboot.security.services.UserDetailsServiceImpl;
import com.springboot.service.EmailService;
import com.springboot.service.OtpService;
import com.springboot.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            otpService.deleteOtpByUsername(loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo mã OTP và lưu vào cơ sở dữ liệu
            Otp otp = otpService.generateOtp(loginRequest.getUsername());

            // Lấy thông tin user để gửi email
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String email = userDetails.getEmail();

            // Gửi email OTP
            String subject = "Your OTP Code";
            String text = "Your OTP code is: " + otp.getOtpCode();
            emailService.sendOtpMessage(email, subject, text);

            return ResponseEntity.ok(new OtpResponse(otp.getOtpCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Sai mật khẩu hoặc tên đăng nhập");
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndGenerateToken(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean isOtpValid = otpService.verifyOtp(verifyOtpRequest.getUsername(), verifyOtpRequest.getOtpCode());

        if (isOtpValid) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(verifyOtpRequest.getUsername());
            String jwt = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            Long id = ((UserDetailsImpl) userDetails).getId();
            String username = userDetails.getUsername();
            String email = ((UserDetailsImpl) userDetails).getEmail();
            JwtResponse jwtResponse = new JwtResponse(jwt, id, username, email);
            return ResponseEntity.ok(jwtResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP is invalid or has expired");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("Dang ky thanh cong!"));
    }
}