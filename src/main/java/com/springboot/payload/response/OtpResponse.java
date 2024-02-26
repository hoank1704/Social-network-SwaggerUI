package com.springboot.payload.response;

public class OtpResponse {
    private String otpCode;

    public OtpResponse(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
