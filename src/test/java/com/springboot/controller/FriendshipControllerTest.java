//package com.springboot.controller;
//
//import com.springboot.controllers.FriendshipController;
//import com.springboot.dto.FriendshipDTO;
//import com.springboot.dto.UserFriendDTO;
//import com.springboot.entities.Friendship;
//import com.springboot.entities.User;
//import com.springboot.repository.FriendshipRepository;
//import com.springboot.repository.UserRepository;
//import com.springboot.security.services.UserDetailsImpl;
//import com.springboot.service.FriendshipService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.Assert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.lang.reflect.Field;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class FriendshipControllerTest {
//    @InjectMocks
//    private FriendshipController friendshipController;
//
//    @Mock
//    private FriendshipService friendshipService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private FriendshipRepository friendshipRepository;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private Principal principal;
//
//    @Mock
//    private Authentication authentication;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    public void testRejectFriendRequest() throws Exception {
//        // Arrange
//        Long friendshipId = 1L;
//        long loggedInUserId = 1L;
//
//        UserDetailsImpl userDetails = new UserDetailsImpl();
//        userDetails.setId(loggedInUserId);
//
//        User loggedInUser = new User();
//        loggedInUser.setId(loggedInUserId);
//
//        Friendship friendship = new Friendship();
//        friendship.setId(friendshipId);
//
//        Authentication authenticationMock = mock(Authentication.class);
//        SecurityContext securityContextMock = mock(SecurityContext.class);
//        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
//        SecurityContextHolder.setContext(securityContextMock);
//        //Mockito.when(authenticationMock.getPrincipal()).thenReturn(userDetails);
//
//        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userRepository.findById(loggedInUserId)).thenReturn(Optional.of(loggedInUser));
//        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
//
//        // Act
//        friendshipController.rejectFriendRequest(friendshipId);
//
//        // Assert
//        verify(friendshipService, Mockito.times(1)).rejectFriendRequest(friendship, loggedInUser);
//    }
//
//
//
//    @Test
//    public void testAcceptFriendRequest() throws Exception {
//        // Arrange
//        Long friendshipId = 1L;
//        String currentUsername = "username";
//
//        when(principal.getName()).thenReturn(currentUsername);
//
//        User currentUser = mock(User.class);
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));
//
//        Friendship friendship = mock(Friendship.class);
//        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
//
//        // Act
//        friendshipController.acceptFriendRequest(friendshipId, principal);
//
//        // Assert
//        verify(friendshipService).acceptFriendRequest(friendship, currentUser);
//        // Add more assertions based on your requirements
//    }
//
//
//    @Test
//    public void getFriends_ValidRequest_Success() {
//        // Arrange
//        long userId = 1L;
//
//        // Tạo mock objects của các dependency cần thiết
//        UserRepository userRepository = mock(UserRepository.class);
//        FriendshipService friendshipService = mock(FriendshipService.class);
//
//        // Set up behavior của các mock objects
//        User user = new User(userId);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        List<User> friends = new ArrayList<>();
//        User friend1 = new User(2L);
//        friend1.setUsername("friend1");
//        friend1.setEmail("friend1@example.com");
//        User friend2 = new User(3L);
//        friend2.setUsername("friend2");
//        friend2.setEmail("friend2@example.com");
//        friends.add(friend1);
//        friends.add(friend2);
//        when(friendshipService.getFriends(user)).thenReturn(friends);
//
//        // Act
//        FriendshipController friendshipController = new FriendshipController();
//
//        try {
//            // Sử dụng Reflection để truy cập các trường private trong lớp FriendshipController
//            Field userRepositoryField = FriendshipController.class.getDeclaredField("userRepository");
//            userRepositoryField.setAccessible(true);
//            userRepositoryField.set(friendshipController, userRepository);
//
//            Field friendshipServiceField = FriendshipController.class.getDeclaredField("friendshipService");
//            friendshipServiceField.setAccessible(true);
//            friendshipServiceField.set(friendshipController, friendshipService);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        ResponseEntity<?> response = friendshipController.getFriends(userId);
//
//        // Assert
//        // Kiểm tra xem phương thức getFriends của friendshipService đã được gọi với đúng tham số chưa
//        verify(friendshipService).getFriends(user);
//        // Kiểm tra xem response có trả về HTTP status code 200 OK không
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        // Kiểm tra xem response body có phải là List<UserFriendDTO> với đúng số lượng và thông tin friend không
//        List<UserFriendDTO> expectedFriendDTOs = new ArrayList<>();
//        UserFriendDTO friendDTO1 = new UserFriendDTO(friend1.getId(), friend1.getUsername(), friend1.getEmail());
//        UserFriendDTO friendDTO2 = new UserFriendDTO(friend2.getId(), friend2.getUsername(), friend2.getEmail());
//        expectedFriendDTOs.add(friendDTO1);
//        expectedFriendDTOs.add(friendDTO2);
//        assertEquals(expectedFriendDTOs, response.getBody());
//    }
//
//}
