package com.springboot.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePostResponse {
    private byte[] imageBytes;
    private String imageId;

    public ImagePostResponse(byte[] imageBytes) {
    }
}
