package com.springboot.repository;

import com.springboot.entities.Otp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByUsernameAndOtpCode(String username, String otpCode);

    Otp findByUsername(String username);
    void deleteByUsername(String username);
}
