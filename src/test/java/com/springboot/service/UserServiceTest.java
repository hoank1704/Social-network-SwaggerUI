//package com.springboot.service;
//
//import com.springboot.dto.UserDTO;
//import com.springboot.entities.ERole;
//import com.springboot.entities.Friendship;
//import com.springboot.entities.User;
//import com.springboot.payload.request.SignupRequest;
//import com.springboot.payload.request.UserUpdateRequest;
//import com.springboot.payload.response.AvatarResponse;
//import com.springboot.payload.response.ImagePostResponse;
//import com.springboot.repository.FriendshipRepository;
//import com.springboot.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//    @Mock
//    private FriendshipRepository friendshipRepository;
//    @Mock
//    private ImageService imageService;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    @Test
//    void testGetAvatarUserWithExistingUserAndAvatarUrl() throws IOException {
//        Long userId = 1L;
//        String avatarUrl = "http://example.com/avatar.jpg";
//
//        User user = new User();
//        user.setImageUrl(avatarUrl);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        byte[] imageBytes = "avatar image".getBytes();
//        ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
//
//        when(imageService.getImage(avatarUrl)).thenReturn(imageResource);
//
//        AvatarResponse result = userService.getAvatarUser(userId);
//
//        assertNotNull(result);
//        assertArrayEquals(imageBytes, result.getImageBytes());
//
//        verify(userRepository, times(1)).findById(userId);
//        verify(imageService, times(1)).getImage(avatarUrl);
//    }
//
//    @Test
//    void testGetAvatarUserWithExistingUserAndNullAvatarUrl() throws IOException {
//        Long userId = 1L;
//
//        User user = new User();
//        user.setImageUrl(null);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        byte[] defaultImageBytes = "default avatar image".getBytes();
//        ByteArrayResource defaultImageResource = new ByteArrayResource(defaultImageBytes);
//
//        when(imageService.getImage("logoUser.png")).thenReturn(defaultImageResource);
//
//        AvatarResponse result = userService.getAvatarUser(userId);
//
//        assertNotNull(result);
//        assertArrayEquals(defaultImageBytes, result.getImageBytes());
//
//        verify(userRepository, times(1)).findById(userId);
//        verify(imageService, times(1)).getImage("logoUser.png");
//    }
//
//    @Test
//    void testGetAvatarUserWithNonExistingUser() {
//        Long userId = 1L;
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        AvatarResponse result = userService.getAvatarUser(userId);
//
//        assertNull(result);
//
//        verify(userRepository, times(1)).findById(userId);
//        verifyNoInteractions(imageService);
//    }
//
//    @Test
//    void testGetDefaultAvatar() throws IOException {
//        byte[] defaultImageBytes = "default avatar image".getBytes();
//        ByteArrayResource defaultImageResource = new ByteArrayResource(defaultImageBytes);
//
//        when(imageService.getImage("logoUser.png")).thenReturn(defaultImageResource);
//
//        byte[] result = userService.getDefaultAvatar();
//
//        assertArrayEquals(defaultImageBytes, result);
//
//        verify(imageService, times(1)).getImage("logoUser.png");
//    }
//
//    @Test
//    void testGetDefaultAvatarWhenImageServiceThrowsIOException() throws IOException {
//        when(imageService.getImage("logoUser.png")).thenThrow(IOException.class);
//
//        byte[] result = userService.getDefaultAvatar();
//
//        assertNull(result);
//
//        verify(imageService, times(1)).getImage("logoUser.png");
//    }
//
//
//    @Test
//    void testSignUp_NewUser() {
//        // Arrange
//        SignupRequest signupRequest = new SignupRequest();
//        signupRequest.setUsername("username");
//        signupRequest.setEmail("user@example.com");
//        signupRequest.setPassword("password");
//
//        Mockito.when(userRepository.existsByUsername(Mockito.eq("username"))).thenReturn(false);
//        Mockito.when(userRepository.existsByEmail(Mockito.eq("user@example.com"))).thenReturn(false);
//
//        Mockito.when(passwordEncoder.encode(Mockito.eq("password"))).thenReturn("encoded-password");
//
//        // Act
//        User result = userService.signUp(signupRequest);
//
//        // Assert
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals("username", result.getUsername());
//        Assertions.assertEquals("user@example.com", result.getEmail());
//        Assertions.assertEquals("encoded-password", result.getPassword());
//        Mockito.verify(userRepository).save(Mockito.any(User.class));
//    }
//
//    @Test
//    void testSignUp_UserAlreadyExists() {
//        // Arrange
//        SignupRequest signupRequest = new SignupRequest();
//        signupRequest.setUsername("existinguser");
//        signupRequest.setEmail("user@example.com");
//        signupRequest.setPassword("password");
//
//        Mockito.when(userRepository.existsByUsername(Mockito.eq("existinguser"))).thenReturn(true);
//
//        // Act & Assert
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            userService.signUp(signupRequest);
//        });
//        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
//    }
//
//    @Test
//    void testSignUp_EmailAlreadyExists() {
//        // Arrange
//        SignupRequest signupRequest = new SignupRequest();
//        signupRequest.setUsername("username");
//        signupRequest.setEmail("existinguser@example.com");
//        signupRequest.setPassword("password");
//
//        Mockito.when(userRepository.existsByUsername(Mockito.eq("username"))).thenReturn(false);
//        Mockito.when(userRepository.existsByEmail(Mockito.eq("existinguser@example.com"))).thenReturn(true);
//
//        // Act & Assert
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            userService.signUp(signupRequest);
//        });
//        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
//    }
//
////    @Test
////    void resetPassword_Success() {
////        // Arrange
////        String newPassword = "newPassword";
////        String username = "testUser";
////        User user = new User();
////
////        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
////        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
////
////        // Act
////        boolean result = userService.resetPassword(newPassword, username);
////
////        // Assert
////        assertTrue(result);
////        assertEquals("encodedPassword", user.getPassword());
////        verify(userRepository, times(1)).save(user);
////    }
////
////    @Test
////    void resetPassword_ReturnsFalse() {
////        // Arrange
////        String newPassword = "newPassword";
////        String username = "testUser";
////
////        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
////
////        // Act
////        boolean result = userService.resetPassword(newPassword, username);
////
////        // Assert
////        assertFalse(result);
////        verify(userRepository, never()).save(any(User.class));
////    }
//
//    @Test
//    public void testDeleteUser() {
//        // Arrange
//        User user = new User();
//        user.setId(1L);
//
//        List<Friendship> sentInvitations = new ArrayList<>();
//        Friendship invitation1 = new Friendship();
//        invitation1.setId(1L);
//        invitation1.setUser1(user);
//        invitation1.setStatus("pending");
//        sentInvitations.add(invitation1);
//
//        List<Friendship> existingFriendships = new ArrayList<>();
//        Friendship friendship1 = new Friendship();
//        friendship1.setId(2L);
//        friendship1.setUser1(user);
//        friendship1.setUser2(new User());
//        friendship1.setStatus("accepted");
//        existingFriendships.add(friendship1);
//
//        when(friendshipRepository.findByUser1AndStatus(user, "pending")).thenReturn(sentInvitations);
//        when(friendshipRepository.findByUser1OrUser2AndStatus(user, user, "accepted")).thenReturn(existingFriendships);
//
//        // Act
//        userService.deleteUser(user);
//
//        // Assert
//        verify(friendshipRepository, times(1)).deleteAll(sentInvitations);
//        verify(friendshipRepository, times(1)).deleteAll(existingFriendships);
//        verify(userRepository, times(1)).delete(user);
//    }
//
//    @Test
//    public void testPostAvatar() {
//        // Arrange
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("testuser");
//        user.setImageUrl(null);
//
//        String imageUrl = "avatar.jpg";
//
//        User updatedUser = new User();
//        updatedUser.setId(1L);
//        updatedUser.setUsername("testuser");
//        updatedUser.setImageUrl(imageUrl);
//
//        when(userRepository.save(user)).thenReturn(updatedUser);
//
//        // Act
//        User result = userService.postAvatar(user, imageUrl);
//        // Assert
//        assertNotNull(result);
//        assertEquals(updatedUser.getImageUrl(), result.getImageUrl());
//
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    public void testUpdateUser() {
//        // Arrange
//        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
//        userUpdateRequest.setUsername("newusername");
//        userUpdateRequest.setEmail("newemail@example.com");
//        //userUpdateRequest.setBirthDate("1990-01-01");
//        userUpdateRequest.setJob("Engineer");
//        userUpdateRequest.setLocation("City");
//
//        User currentUser = new User();
//        currentUser.setId(1L);
//        currentUser.setUsername("oldusername");
//        currentUser.setEmail("oldemail@example.com");
//        currentUser.setPassword("oldpassword");
//        //currentUser.setBirthDate("1980-01-01");
//        currentUser.setJob("OldJob");
//        currentUser.setLocation("OldLocation");
//
//        User updatedUser = new User();
//        updatedUser.setId(1L);
//        updatedUser.setUsername("newusername");
//        updatedUser.setEmail("newemail@example.com");
//        updatedUser.setPassword("encryptedpassword");
//        //updatedUser.setBirthDate("1990-01-01");
//        updatedUser.setJob("Engineer");
//        updatedUser.setLocation("City");
//
//        when(userRepository.save(currentUser)).thenReturn(updatedUser);
//
//        // Act
//        User result = userService.updateUser(userUpdateRequest, currentUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(updatedUser.getUsername(), result.getUsername());
//        assertEquals(updatedUser.getEmail(), result.getEmail());
//        assertEquals(updatedUser.getBirthDate(), result.getBirthDate());
//        assertEquals(updatedUser.getJob(), result.getJob());
//        assertEquals(updatedUser.getLocation(), result.getLocation());
//
//        verify(userRepository, times(1)).save(currentUser);
//    }
//
//    @Test
//    public void testGetUserById_ExistingUser_ReturnsUserDTO() {
//        // Arrange
//        Long userId = 123L;
//        User user = new User();
//        user.setId(userId);
//        user.setUsername("Username1");
//        user.setImageUrl("http://example.com/image.jpg");
//        //user.setBirthDate(LocalDate.of(1999-10-10));
//        user.setJob("Software Engineer");
//        user.setLocation("HN");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // Act
//        UserDTO result = userService.getUserById(userId);
//
//        // Assert
//        Assertions.assertEquals(userId, result.getId());
//        Assertions.assertEquals(user.getUsername(), result.getUsername());
//        Assertions.assertEquals(user.getImageUrl(), result.getImage());
//        Assertions.assertEquals(user.getBirthDate(), result.getBirthDate());
//        Assertions.assertEquals(user.getJob(), result.getJob());
//        Assertions.assertEquals(user.getLocation(), result.getLocation());
//
//        verify(userRepository, times(1)).findById(userId);
//    }
//    @Test
//    public void testGetUserById_NonExistingUser_ThrowsEntityNotFoundException() {
//        // Arrange
//        Long userId = 1L;
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        Assertions.assertThrows(EntityNotFoundException.class, () -> {
//            userService.getUserById(userId);
//        });
//        verify(userRepository, times(1)).findById(userId);
//    }
//
//    @Test
//    public void testFindByUsername_ExistingUser() {
//        // Arrange
//        String username = "abcdef";
//        User user = new User();
//        user.setUsername(username);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        // Act
//        User result = userService.findByUsername(username);
//        // Assert
//        assertNotNull(result);
//        assertEquals(username, result.getUsername());
//    }
//    @Test
//    public void testFindByUsername_NonExistingUser() {
//        // Arrange
//        String username = "abcdef";
//        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
//    }
//}
