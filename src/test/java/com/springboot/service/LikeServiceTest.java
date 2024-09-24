//package com.springboot.service;
//
//import com.springboot.dto.LikeDTO;
//import com.springboot.entities.Like;
//import com.springboot.entities.Post;
//import com.springboot.entities.User;
//import com.springboot.repository.LikeRepository;
//import com.springboot.repository.PostRepository;
//import com.springboot.repository.UserRepository;
//import com.springboot.service.LikeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.security.Principal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//public class LikeServiceTest {
//    @Mock
//    private LikeRepository likeRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PostRepository postRepository;
//    @Mock
//    private Principal principal;
//
//    @InjectMocks
//    private LikeService likeService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    // Case test UnLike trường hợp ko tìm thấy bài post
//    @Test
//    public void testUnlikePost_PostNotFound() {
//        // Arrange
//        Long postId = 1L;
//        String currentUsername = "testUser";
//        User user = new User();
//        user.setUsername(currentUsername);
//        Principal principal = () -> currentUsername;
//
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(IllegalArgumentException.class, () -> likeService.unlikePost(postId, principal));
//    }
//    // Case test UnLike trường hợp chưa like bài post trước đó
//    @Test
//    public void testUnlikePost_LikeNotFound() {
//        // Arrange
//        Long postId = 1L;
//        String currentUsername = "testUser";
//        User user = new User();
//        user.setUsername(currentUsername);
//        Post post = new Post();
//        post.setId(postId);
//        Principal principal = () -> currentUsername;
//
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(likeRepository.findByUserAndPost(user, post)).thenReturn(null);
//
//        // Act and Assert
//        assertThrows(IllegalArgumentException.class, () -> likeService.unlikePost(postId, principal));
//    }
//    // Case test UnLike trường hợp thành công
//    @Test
//    public void testUnlikePost_Successful() {
//        // Arrange
//        Long postId = 1L;
//        String currentUsername = "testUser";
//        User user = new User();
//        user.setUsername(currentUsername);
//        Post post = new Post();
//        post.setId(postId);
//        Like like = new Like();
//        like.setUser(user);
//        like.setPost(post);
//
//        Principal principal = () -> currentUsername;
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(principal);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(likeRepository.findByUserAndPost(user, post)).thenReturn(like);
//
//        // Act
//        likeService.unlikePost(postId, principal);
//
//        // Assert
//        verify(likeRepository, times(1)).delete(like);
//    }
//
//    // Case test LIKE
//    @Test
//    public void testLikePost() {
//        // Arrange
//        String currentUsername = "testuser";
//        User user = new User();
//        user.setUsername(currentUsername);
//
//        Long postId = 1L;
//        Post post = new Post();
//        post.setId(postId);
//
//        LikeDTO likeDTO = new LikeDTO();
//        likeDTO.setPostId(postId);
//
//        when(principal.getName()).thenReturn(currentUsername);
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(likeRepository.findByUserAndPost(user, post)).thenReturn(null);
//
//        // Act
//        assertDoesNotThrow(() -> likeService.likePost(likeDTO, principal));
//
//        // Assert
//        verify(principal, times(1)).getName();
//        verify(userRepository, times(1)).findByUsername(currentUsername);
//        verify(postRepository, times(1)).findById(postId);
//        verify(likeRepository, times(1)).findByUserAndPost(user, post);
//        verify(likeRepository, times(1)).save(any(Like.class));
//    }
//
//    // Case test Like trường hợp đã like trc đó rồi
//    @Test
//    public void testLikePost_UserAlreadyLiked() {
//        // Arrange
//        String currentUsername = "testuser";
//        User user = new User();
//        user.setUsername(currentUsername);
//
//        Long postId = 1L;
//        Post post = new Post();
//        post.setId(postId);
//
//        LikeDTO likeDTO = new LikeDTO();
//        likeDTO.setPostId(postId);
//
//        Like existingLike = new Like();
//        existingLike.setUser(user);
//        existingLike.setPost(post);
//
//        when(principal.getName()).thenReturn(currentUsername);
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(likeRepository.findByUserAndPost(user, post)).thenReturn(existingLike);
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> likeService.likePost(likeDTO, principal));
//
//        verify(principal, times(1)).getName();
//        verify(userRepository, times(1)).findByUsername(currentUsername);
//        verify(postRepository, times(1)).findById(postId);
//        verify(likeRepository, times(1)).findByUserAndPost(user, post);
//        verify(likeRepository, never()).save(any(Like.class));
//    }
//
//    // Case test Like trường hợp User or Post invalid
//    @Test
//    public void testLikePost_InvalidUserOrPost() {
//        // Arrange
//        String currentUsername = "testuser";
//        User user = null;
//        Long postId = 1L;
//        Post post = null;
//
//        LikeDTO likeDTO = new LikeDTO();
//        likeDTO.setPostId(postId);
//
//        when(principal.getName()).thenReturn(currentUsername);
//        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.ofNullable(user));
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> likeService.likePost(likeDTO, principal));
//
//        verify(principal, times(1)).getName();
//        verify(userRepository, times(1)).findByUsername(currentUsername);
//        verify(postRepository, times(1)).findById(postId);
//        verify(likeRepository, never()).findByUserAndPost(any(User.class), any(Post.class));
//        verify(likeRepository, never()).save(any(Like.class));
//    }
//}
