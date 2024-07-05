//package com.springboot.service;
//
//import com.springboot.entities.Otp;
//import com.springboot.repository.OtpRepository;
//import com.springboot.service.OtpService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.time.LocalDateTime;
//
//public class OtpServiceTest {
//
//    @Mock
//    private OtpRepository otpRepository;
//
//    @InjectMocks
//    private OtpService otpService;
//
//    @Captor
//    private ArgumentCaptor<Otp> otpCaptor;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGenerateOtp() {
//        String email = "testUser@gmail.com";
//
//        Otp generatedOtp = otpService.generateOtp(email);
//        System.out.println(generatedOtp);
//
//        // Kiểm tra kết quả trả về
//        Assertions.assertNotNull(email, generatedOtp.setEmail());
//        Assertions.assertNotNull(generatedOtp.getOtpCode());
//        Assertions.assertNotNull(generatedOtp.getExpirationTime());
//
//        Mockito.verify(otpRepository, Mockito.times(1)).save(otpCaptor.capture());
//
//        // Kiểm tra đối tượng Otp được lưu
//        Otp savedOtp = otpCaptor.getValue();
//        Assertions.assertEquals(email, savedOtp.setEmail());
//        Assertions.assertEquals(generatedOtp.getOtpCode(), savedOtp.getOtpCode());
//        Assertions.assertEquals(generatedOtp.getExpirationTime(), savedOtp.getExpirationTime());
//    }
//
//    @Test
//    void testVerifyValidOtp() {
//        // Chuẩn bị dữ liệu đầu vào
//        String username = "testUser";
//        String otpCode = "123456";
//
//        // Tạo một đối tượng Otp hợp lệ
//        Otp validOtp = new Otp();
//        validOtp.setEmail(username);
//        validOtp.setOtpCode(otpCode);
//        validOtp.setExpirationTime(LocalDateTime.now().plusMinutes(1));
//
//        // Giả lập phương thức findByUsernameAndOtpCode() trên otpRepository để trả về đối tượng Otp hợp lệ
//        Mockito.when(otpRepository.findByUsernameAndOtpCode(username, otpCode)).thenReturn(validOtp);
//
//        // Gọi phương thức verifyOtp()
//        boolean result = otpService.verifyOtp(username, otpCode);
//
//        // Kiểm tra kết quả trả về
//        Assertions.assertTrue(result);
//
//        // Kiểm tra xem phương thức delete() trên otpRepository đã được gọi với đối tượng Otp hợp lệ
//        Mockito.verify(otpRepository, Mockito.times(1)).delete(validOtp);
//    }
//
//    @Test
//    void testVerifyInvalidOtp() {
//        // Chuẩn bị dữ liệu đầu vào
//        String username = "testUser";
//        String otpCode = "123456";
//
//        // Giả lập phương thức findByUsernameAndOtpCode() trên otpRepository để trả về null
//        Mockito.when(otpRepository.findByUsernameAndOtpCode(username, otpCode)).thenReturn(null);
//
//        // Gọi phương thức verifyOtp()
//        boolean result = otpService.verifyOtp(username, otpCode);
//
//        // Kiểm tra kết quả trả về
//        Assertions.assertFalse(result);
//
//        // Kiểm tra xem phương thức delete() trên otpRepository không được gọi
//        Mockito.verify(otpRepository, Mockito.never()).delete(Mockito.any(Otp.class));
//    }
//}
