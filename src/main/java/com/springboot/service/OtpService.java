package com.springboot.service;

import com.springboot.entities.Otp;
import com.springboot.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    public Otp generateOtp(String username) {

        otpRepository.deleteByUsername(username);
        // Tạo mã OTP ngẫu nhiên
        String otpCode = generateRandomOtpCode();

        // Thiết lập thời gian hết hạn (5 phút từ thời điểm hiện tại)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        // Lưu OTP vào cơ sở dữ liệu
        Otp otp = new Otp();
        otp.setUsername(username);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);
        otpRepository.save(otp);

        return otp;
    }

    public void deleteOtpByUsername(String username) {
        Otp otp = otpRepository.findByUsername(username);
        if (otp != null) {
            otpRepository.delete(otp);
        }
    }

    public boolean verifyOtp(String username, String otpCode) {
        // Kiểm tra xem mã OTP có hợp lệ hay không
        Otp otp = otpRepository.findByUsernameAndOtpCode(username, otpCode);
        if (otp != null && !otp.isExpired()) {
            // Xóa OTP sau khi xác thực thành công
            otpRepository.delete(otp);
            return true;
        }
        return false;
    }

    public String generateRandomOtpCode() {
        int otpLength = 6; // Độ dài của mã OTP
        String allowedChars = "0123456789";

        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        // Tạo mã OTP ngẫu nhiên bằng cách chọn ngẫu nhiên các ký tự từ `allowedChars`
        for (int i = 0; i < otpLength; i++) {
            int index = random.nextInt(allowedChars.length());
            otp.append(allowedChars.charAt(index));
        }
        return otp.toString();
    }
}
