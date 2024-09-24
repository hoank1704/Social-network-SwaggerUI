//package com.springboot.controller;
//
//import com.springboot.controllers.LikeController;
//import com.springboot.dto.LikeDTO;
//import com.springboot.repository.PostRepository;
//import com.springboot.service.LikeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.security.Principal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class LikeControllerTest {
//    @Mock
//    private PostRepository postRepository;
//    @Mock
//    private LikeService likeService;
//    @InjectMocks
//    private LikeController likeController;
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    public void testLikePost_Success() {
//        // Arrange
//        Long postId = 1L;
//        Principal principal = mock(Principal.class);
//
//        doNothing().when(likeService).likePost(any(LikeDTO.class), any(Principal.class));
//
//        // Act
//        ResponseEntity<?> response = likeController.likePost(postId, principal);
//
//        // Assert
//        verify(likeService, times(1)).likePost(any(LikeDTO.class), any(Principal.class));
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Đã like", response.getBody());
//    }
//
//    @Test
//    public void testLikePost_AlreadyLiked() {
//        // Arrange
//        Long postId = 1L;
//        Principal principal = mock(Principal.class);
//
//        doThrow(IllegalArgumentException.class).when(likeService).likePost(any(LikeDTO.class), any(Principal.class));
//
//        // Act
//        ResponseEntity<?> response = likeController.likePost(postId, principal);
//
//        // Assert
//        verify(likeService, times(1)).likePost(any(LikeDTO.class), any(Principal.class));
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Bạn đã like post này rồi", response.getBody());
//    }
//
//    @Test
//    public void testUnlikePost_Success() {
//        // Arrange
//        Long postId = 1L;
//        Principal principal = mock(Principal.class);
//
//        doNothing().when(likeService).unlikePost(any(Long.class), any(Principal.class));
//
//        // Act
//        ResponseEntity<?> response = likeController.unlikePost(postId, principal);
//
//        // Assert
//        verify(likeService, times(1)).unlikePost(any(Long.class), any(Principal.class));
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Đã unlike", response.getBody());
//    }
//
//    @Test
//    public void testUnlikePost_AlreadyUnliked() {
//        // Arrange
//        Long postId = 1L;
//        Principal principal = mock(Principal.class);
//
//        doThrow(IllegalArgumentException.class).when(likeService).unlikePost(any(Long.class), any(Principal.class));
//
//        // Act
//        ResponseEntity<?> response = likeController.unlikePost(postId, principal);
//
//        // Assert
//        verify(likeService, times(1)).unlikePost(any(Long.class), any(Principal.class));
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Bạn đã unlike post này rồi", response.getBody());
//    }
//}
