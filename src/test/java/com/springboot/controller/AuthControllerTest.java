//package com.springboot.controller;
//
//import com.springboot.controllers.AuthController;
//import com.springboot.entities.Otp;
//import com.springboot.payload.request.LoginRequest;
//import com.springboot.payload.request.SignupRequest;
//import com.springboot.payload.request.VerifyOtpRequest;
//import com.springboot.payload.response.JwtResponse;
//import com.springboot.payload.response.MessageResponse;
//import com.springboot.payload.response.OtpResponse;
//import com.springboot.security.jwt.JwtUtils;
//import com.springboot.security.services.UserDetailsImpl;
//import com.springboot.service.OtpService;
//import com.springboot.service.UserService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//
//class AuthControllerTest {
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @Mock
//    private OtpService otpService;
//
//    @Mock
//    private UserDetailsService userDetailsService;
//
//    @InjectMocks
//    private AuthController authController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Thiết lập đối tượng Authentication trong SecurityContextHolder
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    @Test
//    void testAuthenticateUser() {
//        // Arrange
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setEmail("username");
//        loginRequest.setPassword("password");
//
//        Otp otp = new Otp();
//        otp.setOtpCode("123456");
//
//        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(Mockito.mock(Authentication.class));
//        Mockito.when(otpService.generateOtp(eq("username"))).thenReturn(otp);
//
//        // Act
//        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        Assertions.assertTrue(responseEntity.getBody() instanceof OtpResponse);
//        OtpResponse otpResponse = (OtpResponse) responseEntity.getBody();
//        Assertions.assertEquals("123456", otpResponse.getOtpCode());
//    }
//
////    @Test
////    void testVerifyOtpAndGenerateToken_ValidOtp() {
////        // Arrange
////        VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest();
////        verifyOtpRequest.setUsername("username");
////        verifyOtpRequest.setOtpCode("123456");
////
////        UserDetails userDetails = new UserDetailsImpl();
////        userDetails.setUsername("username");
////        userDetails.setAuthorities(Collections.emptyList());
////
////        Mockito.when(otpService.verifyOtp(eq("username"), eq("123456"))).thenReturn(true);
////        Mockito.when(userDetailsService.loadUserByUsername(eq("username"))).thenReturn(userDetails);
////        Mockito.when(jwtUtils.generateJwtToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("jwt-token");
////
////        // Act
////        ResponseEntity<?> responseEntity = authController.verifyOtpAndGenerateToken(verifyOtpRequest);
////
////        // Assert
////        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
////        Assertions.assertTrue(responseEntity.getBody() instanceof JwtResponse);
////        JwtResponse jwtResponse = (JwtResponse) responseEntity.getBody();
////        Assertions.assertEquals("jwt-token", jwtResponse.getToken());
////        Assertions.assertEquals("username", jwtResponse.getUsername());
////    }
//
//    @Test
//    void testVerifyOtpAndGenerateToken_InvalidOtp() {
//        // Arrange
//        VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest();
//        verifyOtpRequest.setEmail("username");
//        verifyOtpRequest.setOtpCode("123456");
//
//        Mockito.when(otpService.verifyOtp(eq("username"), eq("123456"))).thenReturn(false);
//
//        // Act
//        ResponseEntity<?> responseEntity = authController.verifyOtpAndGenerateToken(verifyOtpRequest);
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
//        Assertions.assertEquals("OTP is invalid or has expired", responseEntity.getBody());
//    }
//
//    @Test
//    void testRegisterUser() {
//        // Arrange
//        SignupRequest signupRequest = new SignupRequest();
//        signupRequest.setUsername("username");
//        signupRequest.setPassword("password");
//
//        // Act
//        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        Assertions.assertTrue(responseEntity.getBody() instanceof MessageResponse);
//        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
//        Assertions.assertEquals("Dang ky thanh cong!", messageResponse.getMessage());
//        Mockito.verify(userService).signUp(signupRequest);
//    }
//}
