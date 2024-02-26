package com.springboot.controllers;

import com.springboot.dto.UserDTO;
import com.springboot.entities.User;
import com.springboot.exportExcel.ExportExcel;
import com.springboot.payload.request.UserUpdateRequest;
import com.springboot.payload.response.AvatarResponse;
import com.springboot.payload.response.ImagePostResponse;
import com.springboot.security.jwt.JwtUtils;
import com.springboot.service.FriendshipService;
import com.springboot.service.ImageService;
import com.springboot.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ImageService imageService;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private HttpServletRequest request;

    private List<String> revokedTokens = new ArrayList<>();

    // Xuất file excel
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response, Principal principal) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        User loggedInUser = userService.findByUsername(principal.getName());

        ExportExcel exportExcelService = new ExportExcel(loggedInUser);

        exportExcelService.export(response);
    }

    // get token change Password
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/getTokenToChangePassword")
    public ResponseEntity<String> getToken(@RequestParam("email") @RequestBody String email){
        try {
            String jwtToken = jwtUtils.generateJwtTokenToChangePassword(email);
            return ResponseEntity.ok("JWT token for email " + email + ": " + jwtToken);
        } catch (Exception e) {
            throw new IllegalStateException("Error generating JWT token for email " + email);
        }
    }

    // forgot Password
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("newPassword") String newPassword,
                                                @RequestParam("token") String token,
                                                Principal principal) {
        String username = principal.getName();
        if (userService.resetPassword(newPassword, username, token)) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired token.");
        }
    }

    // Lay User theo Id
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.getUserById(id);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@RequestParam(value = "image") MultipartFile image,
                                             Principal principal) throws IOException {
        try {
            String currentUser = principal.getName();
            User user = userService.findByUsername(currentUser);

            String imageUrl = imageService.uploadImage(image);
            User avatarUser = userService.postAvatar(user, imageUrl);

            return new ResponseEntity<>(avatarUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Upload avatar failed.", HttpStatus.BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getUsersAvatar/{userId}")
    public ResponseEntity<byte[]> getUsersAvatar(@PathVariable Long userId) {
        AvatarResponse image = userService.getAvatarUser(userId);

        if (image != null) {
            byte[] avatar = image.getImageBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
        }
        // Trả về hình ảnh mặc định nếu không có avatar
        try {
            ByteArrayResource defaultImageResource = imageService.getImage("logoUser.png");
            byte[] defaultAvatar = defaultImageResource.getInputStream().readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(defaultAvatar, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Cap nhat thong tin
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                           Principal principal) {
        try {
            String currentUser = principal.getName();
            User user = userService.findByUsername(currentUser);

            User updatedUser = userService.updateUser(userUpdateRequest, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed.", HttpStatus.BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping
    public void deleteUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        userService.deleteUser(user);
    }
}
