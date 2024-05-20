package com.springboot.service;

import com.springboot.dto.CommentDTO;
import com.springboot.entities.Comment;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import com.springboot.payload.request.CommentUpdateRequest;
import com.springboot.payload.response.CommentNotFoundException;
import com.springboot.payload.response.UnauthorizedException;
import com.springboot.repository.CommentRepository;
import com.springboot.repository.PostRepository;
import com.springboot.repository.UserRepository;
import com.springboot.service.CommentService;
import com.springboot.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


//    @Test
//    void deleteComment_WhenCommentExistsAndUserIsOwner_ShouldDeleteComment() {
//        // Arrange
//        Long commentId = 1L;
//        User loggedInUser = new User(1L);
//        Comment comment = new Comment();
//        comment.setPost(new Post());
//        comment.getPost().setUser(new User());
//        comment.setUser(loggedInUser);
//        comment.getPost().getUser().setId(loggedInUser.getId());
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//
//        // Act
//        commentService.deleteComment(commentId, loggedInUser);
//
//        // Assert
//        verify(commentRepository, times(1)).delete(comment);
//    }

    @Test
    void deleteComment_WhenCommentExistsAndUserIsNotOwner_ShouldThrowUnauthorizedException() {
        // Arrange
        Long commentId = 1L;
        User loggedInUser = new User(1L);
        Comment comment = new Comment();
        comment.setUser(new User(2L)); // Set different user ID for the comment
        comment.setPost(new Post()); // Initialize the post object
        comment.getPost().setUser(new User(3L)); // Set different post owner ID
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            commentService.deleteComment(commentId, loggedInUser);
        });
    }

    @Test
    void deleteComment_WhenCommentNotFound_ShouldThrowCommentNotFoundException() {
        // Arrange
        Long commentId = 1L;
        User loggedInUser = new User(1L);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            commentService.deleteComment(commentId, loggedInUser);
        });
    }

    // Case test lấy ra comment theo userId
    @Test
    void getCommentsByUserId() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Comment comment1 = new Comment();
        comment1.setContent("Comment 1");
        comment1.setCreatedDate(LocalDateTime.now());
        comment1.setPost(new Post());
        comment1.getPost().setId(1L);

        Comment comment2 = new Comment();
        comment2.setContent("Comment 2");
        comment2.setCreatedDate(LocalDateTime.now());
        comment2.setPost(new Post());
        comment2.getPost().setId(2L);

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findByUserIdAndDeleted(userId, false)).thenReturn(comments);

        // Act
        List<CommentDTO> commentDTOs = commentService.getCommentsByUserId(userId);

        // Assert
        assertEquals(2, commentDTOs.size());
        assertEquals("Comment 1", commentDTOs.get(0).getContent());
        assertEquals(comment1.getCreatedDate(), commentDTOs.get(0).getCreatedDate());
        assertEquals(1L, commentDTOs.get(0).getPostId());
        assertEquals("Comment 2", commentDTOs.get(1).getContent());
        assertEquals(comment2.getCreatedDate(), commentDTOs.get(1).getCreatedDate());
        assertEquals(2L, commentDTOs.get(1).getPostId());

        //verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, times(1)).findByUserIdAndDeleted(userId, false);
    }

    // Case test update Comment trường hợp Mismatching userId
    @Test
    void updateComment_MismatchingUserId() {
        // Arrange
        Long commentId = 1L;
        Long userId = 2L;
        String newContent = "Updated content";

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setContent("Old content");
        User commentUser = new User();
        commentUser.setId(3L); // User ID different from the provided one
        existingComment.setUser(commentUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> {
            commentService.updateComment(commentId, newContent, userId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
    }

    // Case test update Comment trường hợp k tìm thấy Cmt theo id
    @Test
    public void testUpdateComment_CommentNotFound() {

        Long commentId = 1L;
        String newContent = "Updated comment";
        Long userId = 123L;

        // Thiết lập hành vi
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Gọi phương thức cần test
        try {
            commentService.updateComment(commentId, newContent, userId);
        } catch (EntityNotFoundException e) {
            assertEquals("Comment not found", e.getMessage());
        }
    }

    // Case test update Comment thành công
    @Test
    public void testUpdateComment() {
        // Tạo các đối tượng giả
        Long commentId = 1L;
        String newContent = "Updated comment";
        Long userId = 123L;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Old comment");
        User user = new User();
        user.setId(userId);
        comment.setUser(user);

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent(newContent);

        CommentUpdateRequest expectedResponse = new CommentUpdateRequest();
        expectedResponse.setContent(newContent);

        // Thiết lập hành vi giả cho các phương thức của đối tượng giả
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(updatedComment);

        // Gọi phương thức cần kiểm thử
        CommentUpdateRequest result = commentService.updateComment(commentId, newContent, userId);

        // Kiểm tra kết quả
        assertEquals(expectedResponse.getContent(), result.getContent());
    }

    // Case test create Comment
    @Test
    public void testCreateComment_Successful() {
        String username = "user1";
        String content = "This is a comment";
        Long postId = 1L;

        // Tạo đối tượng User
        User user = new User();
        user.setUsername(username);

        // Tạo đối tượng Post
        Post post = new Post();
        post.setId(postId);

        // Tạo đối tượng CommentDTO
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        //commentDTO.setCreatedDate(LocalDateTime.parse("2022-01-01"));
        commentDTO.setPostId(postId);

        // Giả lập phương thức findByUsername trả về Optional chứa User
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Giả lập phương thức findById trả về Optional chứa Post
        when(postRepository.findById(Long.valueOf(postId))).thenReturn(Optional.of(post));

        // Giả lập phương thức save trả về Comment đã tạo
        Comment createdComment = new Comment();
        createdComment.setContent(content);
        createdComment.setCreatedDate(commentDTO.getCreatedDate());
        createdComment.setPost(post);
        createdComment.setUser(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(createdComment);

        // Gọi phương thức createComment
        CommentDTO result = commentService.createComment(commentDTO, username);

        // Kiểm tra xem CommentDTO đã được tạo và trả về đúng thông tin
        Assertions.assertEquals(content, result.getContent());
        Assertions.assertEquals(commentDTO.getCreatedDate(), result.getCreatedDate());
        Assertions.assertEquals(postId, result.getPostId());
    }
}
