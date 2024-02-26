package com.springboot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private MultipartFile multipartFile;

    private String realPathtoUploads;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        realPathtoUploads = "D:\\PRJ-home\\springboot\\src\\main\\java\\com\\springboot\\uploads\\";
    }


//    @Test
//    void testUploadImage() throws IOException {
//        // Mock input data
//
//        String orgName = "test.jpg";
//        String filePath = realPathtoUploads + orgName;
//        when(multipartFile.getOriginalFilename()).thenReturn(orgName);
//        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
//
//        // Call the method
//        String uploadedFileName = imageService.uploadImage(multipartFile);
//
//        // Check if the file is created
//        File uploadedFile = new File(filePath);
//        assertTrue(uploadedFile.exists());
//
//        // Clean up
//        uploadedFile.delete();
//    }

    @Test
    void testGetImage() throws IOException {
        // Arrange
        byte[] fileContent = "Test file content".getBytes();
        Files.write(Paths.get(realPathtoUploads + "image.jpg"), fileContent);

        // Act
        ByteArrayResource result = imageService.getImage("image.jpg");

        // Assert
        Assertions.assertArrayEquals(fileContent, result.getByteArray());

        // Clean up
        Files.deleteIfExists(Paths.get(realPathtoUploads + "image.jpg"));
    }

    @Test
    void testResizeImage() throws IOException {
        // Create a test image
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);

        // Call the method
        BufferedImage resizedImage = imageService.resizeImage(image, 200, 200);

        // Check the dimensions of the resized image
        assertEquals(200, resizedImage.getWidth());
        assertEquals(200, resizedImage.getHeight());
    }

}