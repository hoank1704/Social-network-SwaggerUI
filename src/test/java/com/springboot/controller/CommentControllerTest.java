package com.springboot.controller;

import com.springboot.controllers.CommentController;
import com.springboot.dto.CommentDTO;
import com.springboot.dto.CommentDTO2;
import com.springboot.entities.User;
import com.springboot.payload.request.CommentUpdateRequest;
import com.springboot.repository.CommentRepository;
import com.springboot.service.CommentService;
import com.springboot.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // Case test createComment
    @Test
    public void testCreateComment() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("username");

        // Act
        ResponseEntity<?> response = commentController.createComment(commentDTO, principal);

        // Assert
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("Created comment successfully.", response.getBody());
        Mockito.verify(commentService).createComment(commentDTO, "username");
    }
    @Test
    public void testCreateComment_WithException() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        Principal principal = mock(Principal.class);
        String username = "testUser";
        String errorMessage = "Error occurred while creating comment.";

        when(principal.getName()).thenReturn(username);
        doThrow(new RuntimeException(errorMessage)).when(commentService).createComment(commentDTO, username);

        // Act
        ResponseEntity<?> response = commentController.createComment(commentDTO, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());

        // Verify mock interactions
        verify(principal, times(1)).getName();
        verify(commentService, times(1)).createComment(commentDTO, username);
    }


    // Case test updateComment
    @Test
    public void testUpdateComment() {
        // Arrange
        Long commentId = 1L;
        CommentUpdateRequest commentDTO = new CommentUpdateRequest();
        commentDTO.setContent("Updated content");

        Principal principal = mock(Principal.class);
        String username = "testUser";
        User currentUser = new User();
        currentUser.setId(1L);

        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(currentUser);

        CommentUpdateRequest updatedComment = new CommentUpdateRequest();
        updatedComment.setContent("Updated content");

        when(commentService.updateComment(commentId, commentDTO.getContent(), currentUser.getId())).thenReturn(updatedComment);

        // Act
        ResponseEntity<?> response = commentController.updateComment(commentId, commentDTO, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedComment, response.getBody());

        // Verify mock interactions
        verify(principal, times(1)).getName();
        verify(userService, times(1)).findByUsername(username);
        verify(commentService, times(1)).updateComment(commentId, commentDTO.getContent(), currentUser.getId());
    }

    // Case test deleteComment
    @Test
    public void testDeleteComment() {
        // Arrange
        Long commentId = 1L;

        Principal principal = mock(Principal.class);
        String username = "testUser";
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(loggedInUser);

        // Act
        ResponseEntity<?> response = commentController.deleteComment(commentId, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());

        // Verify mock interactions
        verify(principal, times(1)).getName();
        verify(userService, times(1)).findByUsername(username);
        verify(commentService, times(1)).deleteComment(commentId, loggedInUser);
    }


    @Test
    public void testGetCommentsByPostId() {
        // Arrange
        Long postId = 1L;
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);

        List<CommentDTO2> commentDTOs = new ArrayList<>();
        commentDTOs.add(new CommentDTO2());
        commentDTOs.add(new CommentDTO2());

        Page<CommentDTO2> commentPage = new PageImpl<>(commentDTOs, pageable, commentDTOs.size());

        when(commentRepository.findByPostId(postId, pageable)).thenReturn(commentPage);

        // Act
        ResponseEntity<?> response = commentController.getCommentsByPostId(postId, page, size);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDTOs, response.getBody());

        // Verify mock interactions
        verify(commentRepository, times(1)).findByPostId(postId, pageable);
    }

    @Test
    public void testGetCommentsByUserId() {
        // Arrange
        Long userId = 1L;

        List<CommentDTO> commentDTOs = new ArrayList<>();
        commentDTOs.add(new CommentDTO());
        commentDTOs.add(new CommentDTO());

        when(commentService.getCommentsByUserId(userId)).thenReturn(commentDTOs);

        // Act
        ResponseEntity<?> response = commentController.getCommentsByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDTOs, response.getBody());

        // Verify mock interactions
        verify(commentService, times(1)).getCommentsByUserId(userId);
    }

}
