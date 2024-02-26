package com.springboot.service;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final String realPathtoUploads =  "D:\\PRJ-home\\springboot\\src\\main\\java\\com\\springboot\\uploads\\";

    public String uploadImage(MultipartFile multipartFile) throws IOException {
        if(! new File(realPathtoUploads).exists()) {
            new File(realPathtoUploads).mkdir();
        }

        String orgName = multipartFile.getOriginalFilename();
        String filePath = realPathtoUploads + orgName;
        Integer i = 0;
        while (new File(filePath).exists()){
            i++;
            filePath = realPathtoUploads + i + orgName;
        }
        File file = new File(filePath);

        BufferedImage image = ImageIO.read(multipartFile.getInputStream());
        BufferedImage result = resizeImage(image, image.getWidth()/(image.getHeight()/250), 260);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "jpeg", baos);

        MultipartFile multipartFile1 = new MockMultipartFile(multipartFile.getName(), multipartFile.getOriginalFilename(),
                                                            multipartFile.getContentType(), baos.toByteArray());
        multipartFile1.transferTo(file);

        if(i != 0) {
            return i + orgName;
        }
        return orgName;
    }

    public ByteArrayResource getImage (String address) throws IOException{
        return new ByteArrayResource(Files.readAllBytes(Paths.get(realPathtoUploads + address)));
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
