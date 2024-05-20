package com.springboot.controller;

import com.springboot.controllers.UserController;
import com.springboot.dto.UserDTO;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import com.springboot.exportExcel.ExportExcel;
import com.springboot.payload.request.UserUpdateRequest;
import com.springboot.payload.response.AvatarResponse;
import com.springboot.payload.response.ImagePostResponse;
import com.springboot.security.jwt.JwtUtils;
import com.springboot.service.ImageService;
import com.springboot.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ImageService imageService;

    @Mock
    private ExportExcel exportExcelService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


//    @Test
//    void exportToExcel_ExportExcelData_Success() throws IOException {
//        // Arrange
//        Principal principal = mock(Principal.class);
//        when(principal.getName()).thenReturn("testUser");
//        User loggedInUser = new User();
//        loggedInUser.setUsername("testUser");
//        when(userService.findByUsername("testUser")).thenReturn(loggedInUser);
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        // Act
//        userController.exportToExcel(response, principal);
//
//        // Assert
//        assertEquals("application/octet-stream", response.getContentType());
//        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename=users_"));
//        assertNotNull(response.getContentAsByteArray());
//    }

    @Test
    void testGetUsersAvatarWithExistingUserAvatar() throws IOException {
        Long userId = 1L;
        byte[] imageBytes = "avatar image".getBytes();
        AvatarResponse avatarResponse = new AvatarResponse(imageBytes);

        when(userService.getAvatarUser(userId)).thenReturn(avatarResponse);

        ResponseEntity<byte[]> result = userController.getUsersAvatar(userId);

        assertNotNull(result);
        assertArrayEquals(imageBytes, result.getBody());
        HttpHeaders headers = result.getHeaders();
        assertEquals(MediaType.IMAGE_JPEG, headers.getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(userService, times(1)).getAvatarUser(userId);
        verifyNoInteractions(imageService);
    }

    @Test
    void testGetUsersAvatarWithNonExistingUserAvatar() throws IOException {
        Long userId = 1L;
        byte[] defaultImageBytes = "default avatar image".getBytes();
        ByteArrayResource defaultImageResource = new ByteArrayResource(defaultImageBytes);

        when(userService.getAvatarUser(userId)).thenReturn(null);
        when(imageService.getImage("logoUser.png")).thenReturn(defaultImageResource);

        ResponseEntity<byte[]> result = userController.getUsersAvatar(userId);

        assertNotNull(result);
        assertArrayEquals(defaultImageBytes, result.getBody());
        HttpHeaders headers = result.getHeaders();
        assertEquals(MediaType.IMAGE_JPEG, headers.getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(userService, times(1)).getAvatarUser(userId);
        verify(imageService, times(1)).getImage("logoUser.png");
    }

    @Test
    void testGetUsersAvatarWhenDefaultImageServiceThrowsIOException() throws IOException {
        Long userId = 1L;

        when(userService.getAvatarUser(userId)).thenReturn(null);
        when(imageService.getImage("logoUser.png")).thenThrow(IOException.class);

        ResponseEntity<byte[]> result = userController.getUsersAvatar(userId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        verify(userService, times(1)).getAvatarUser(userId);
        verify(imageService, times(1)).getImage("logoUser.png");
    }

    @Test
    public void testGetToken_Success() {
        // Arrange
        String email = "testuser@example.com";
        String jwtToken = "testToken";

        when(jwtUtils.generateJwtTokenToChangePassword(email)).thenReturn(jwtToken);

        // Act
        ResponseEntity<String> response = userController.getToken(email);

        // Assert
        verify(jwtUtils, times(1)).generateJwtTokenToChangePassword(email);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("JWT token for email testuser@example.com: testToken", response.getBody());
    }

//    @Test
//    void resetPassword_ValidRequest_ReturnsOkResponse() {
//        // Arrange
//        String newPassword = "newPassword";
//        Principal principal = mock(Principal.class);
//        when(principal.getName()).thenReturn("testUser");
//        when(userService.resetPassword(newPassword, "testUser")).thenReturn(true);
//
//        // Act
//        ResponseEntity<String> response = userController.resetPassword(newPassword, principal);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Password reset successfully.", response.getBody());
//    }
//
//    @Test
//    void resetPassword_InvalidRequest_ReturnsNotFoundResponse() {
//        // Arrange
//        String newPassword = "newPassword";
//        Principal principal = mock(Principal.class);
//        when(principal.getName()).thenReturn("testUser");
//        when(userService.resetPassword(newPassword, "testUser")).thenReturn(false);
//
//        // Act
//        ResponseEntity<String> response = userController.resetPassword(newPassword, principal);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Email not found", response.getBody());
//    }

    @Test
    public void testGetUserById_Success() {
        // Arrange
        Long id = 1L;
        UserDTO userDTO = mock(UserDTO.class);

        when(userService.getUserById(id)).thenReturn(userDTO);

        // Act
        ResponseEntity<?> response = userController.getUserById(id);

        // Assert
        verify(userService, times(1)).getUserById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        UserUpdateRequest userUpdateRequest = mock(UserUpdateRequest.class);
        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        User updatedUser = mock(User.class);

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername(principal.getName())).thenReturn(user);
        when(userService.updateUser(userUpdateRequest, user)).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(userUpdateRequest, principal);

        // Assert
        verify(userService, times(1)).findByUsername(principal.getName());
        verify(userService, times(1)).updateUser(userUpdateRequest, user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    public void testUploadAvatar_Success() throws IOException {
        // Arrange
        MultipartFile image = new MockMultipartFile("avatar.jpg", new byte[]{1, 2, 3});
        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        String imageUrl = "http://example.com/avatar.jpg";
        User avatarUser = mock(User.class);

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername(principal.getName())).thenReturn(user);
        when(imageService.uploadImage(image)).thenReturn(imageUrl);
        when(userService.postAvatar(user, imageUrl)).thenReturn(avatarUser);

        // Act
        ResponseEntity<?> response = userController.uploadAvatar(image, principal);

        // Assert
        verify(userService, times(1)).findByUsername(principal.getName());
        verify(imageService, times(1)).uploadImage(image);
        verify(userService, times(1)).postAvatar(user, imageUrl);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(avatarUser, response.getBody());
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        String username = "testuser";
        User user = mock(User.class);

        when(authentication.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(user);

        // Act
        userController.deleteUser(authentication);

        // Assert
        verify(userService, times(1)).findByUsername(username);
        verify(userService, times(1)).deleteUser(user);
    }
}
