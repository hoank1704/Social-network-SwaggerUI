//package com.springboot.service;
//
//import com.springboot.dto.PostDTO;
//import com.springboot.entities.Image;
//import com.springboot.entities.Post;
//import com.springboot.entities.User;
//import com.springboot.payload.request.PostRequest;
//import com.springboot.payload.response.ImagePostResponse;
//import com.springboot.repository.*;
//import com.springboot.service.ImageService;
//import com.springboot.service.PostService;
//import org.junit.Assert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import org.springframework.security.access.AccessDeniedException;
//
//import java.io.IOException;
//import java.security.Principal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class PostServiceTest {
//    @Mock
//    private ImageService imageService;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PostRepository postRepository;
//    @Mock
//    private CommentRepository commentRepository;
//    @Mock
//    private LikeRepository likeRepository;
//    @Mock
//    private ImageRepository imageRepository;
//
//    @InjectMocks
//    private PostService postService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//
//    @Test
//    void testGetPostsByIdWithExistingPost() {
//        Long postId = 1L;
//        LocalDateTime createdAt = LocalDateTime.now();
//
//        User user = new User();
//        user.setId(1L);
//
//        Post post = new Post();
//        post.setId(postId);
//        post.setContent("Test post");
//        post.setCreatedAt(createdAt);
//        post.setUser(user);
//        post.setImages(new ArrayList<>());
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(commentRepository.countByPost(post)).thenReturn(5);
//        when(likeRepository.countByPostId(postId)).thenReturn(10);
//
//        PostDTO result = postService.getPostsById(postId);
//
//        assertNotNull(result);
//        assertEquals("Test post", result.getContent());
//        assertEquals(createdAt.toLocalDate().atStartOfDay(), result.getCreatedAt());
//        assertEquals(5, result.getCommentCount());
//        assertEquals(10, result.getLikeCount());
//        assertEquals(1L, result.getUserId());
//        assertEquals("", result.getImageUrl());
//
//        verify(postRepository, times(1)).findById(postId);
//        verify(commentRepository, times(1)).countByPost(post);
//        verify(likeRepository, times(1)).countByPostId(postId);
//    }
//
//    @Test
//    void testGetPostsByIdWithNonExistingPost() {
//        Long postId = 1L;
//
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        PostDTO result = postService.getPostsById(postId);
//
//        assertNull(result);
//
//        verify(postRepository, times(1)).findById(postId);
//        verifyNoInteractions(commentRepository);
//        verifyNoInteractions(likeRepository);
//    }
//
//    @Test
//    public void testCreatePost() {
//        // Arrange
//        PostRequest postRequest = new PostRequest();
//        postRequest.setContent("Test post content");
//        List<String> imageUrls = Arrays.asList("image1.jpg", "image2.jpg");
//        String username = "testuser";
//
//        User user = new User();
//        user.setUsername(username);
//
//        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        Post result = postService.createPost(postRequest, imageUrls, username);
//
//        // Assert
//        Assert.assertNotNull(result);
//        Assert.assertEquals(postRequest.getContent(), result.getContent());
//        Assert.assertEquals(imageUrls.size(), result.getImages().size());
//        Assert.assertEquals(user, result.getUser());
//        Mockito.verify(imageRepository, Mockito.times(imageUrls.size())).save(Mockito.any(Image.class));
//    }
//
//    @Test
//    public void testGetImageByPostId() throws Exception {
//        // Arrange
//        Long postId = 1L;
//        Long imageId = 1L;
//        Post post = new Post();
//        post.setId(postId);
//        Image image = new Image();
//        image.setId(imageId);
//        image.setUrl("image.jpg");
//        post.setImages(Collections.singletonList(image));
//        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        ByteArrayResource imageResource = new ByteArrayResource("test image data".getBytes());
//        Mockito.when(imageService.getImage(Mockito.anyString())).thenReturn(imageResource);
//
//        // Act
//        ImagePostResponse result = postService.getImageByPostId(postId, imageId);
//
//        // Assert
//        Assert.assertNotNull(result);
//        Assert.assertArrayEquals("test image data".getBytes(), result.getImageBytes());
//    }
//
//
//    @Test
//    public void testGetPostByUserId() {
//        // Arrange
//        Long userId = 1L;
//        List<Post> expectedPosts = Arrays.asList(new Post(), new Post());
//        Mockito.when(postRepository.findByUserId(userId)).thenReturn(expectedPosts);
//
//        // Act
//        List<Post> result = postService.getPostByUserId(userId);
//
//        // Assert
//        Assert.assertEquals(expectedPosts.size(), result.size());
//        Assert.assertEquals(expectedPosts, result);
//    }
//
//
//    @Test
//    public void testEditPost_Success() {
//        // Arrange
//        Long postId = 1L;
//        PostRequest postUpdateRequest = new PostRequest();
//        postUpdateRequest.setContent("Updated post content");
//        Principal principal = Mockito.mock(Principal.class);
//        String currentUser = "testuser";
//        Mockito.when(principal.getName()).thenReturn(currentUser);
//
//        User user = new User();
//        user.setUsername(currentUser);
//
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        existingPost.setContent("Old post content");
//        existingPost.setUser(user);
//
//        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        Post result = postService.editPost(postId, postUpdateRequest, principal);
//
//        // Assert
//        Assert.assertNotNull(result);
//        Assert.assertEquals(postUpdateRequest.getContent(), result.getContent());
//        Mockito.verify(postRepository, Mockito.times(1)).save(existingPost);
//    }
//
//
//    @Test
//    public void testGetPostByIdToDelete_Success() {
//        // Arrange
//        Long postId = 1L;
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//
//        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//
//        // Act
//        Post result = postService.getPostByIdToDelete(postId);
//
//        // Assert
//        Assert.assertNotNull(result);
//        Assert.assertEquals(existingPost, result);
//    }
//
//
//
//    @Test
//    public void testGetPostsById_PostNotFound() {
//        // Arrange
//        Long postId = 1L;
//
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        // Act
//        PostDTO postDTO = postService.getPostsById(postId);
//
//        // Assert
//        assertNull(postDTO);
//
//        verify(postRepository, times(1)).findById(postId);
//        verify(commentRepository, never()).countByPost(any(Post.class));
//        verify(likeRepository, never()).countByPostId(anyLong());
//    }
//
//    // Test deletePost() method
//    @Test
//    public void testDeletePost_Success() {
//        // Arrange
//        Long postId = 1L;
//        String username = "john.doe";
//
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        User user = new User();
//        user.setUsername(username);
//        existingPost.setUser(user);
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(username);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Act
//        postService.deletePost(postId);
//
//        // Assert
//        verify(postRepository, times(1)).delete(existingPost);
//    }
//    @Test
//    public void testDeletePost_UserIsNotOwner() {
//        // Arrange
//        Long postId = 1L;
//        String username = "john.doe";
//        String ownerUsername = "jane.doe";
//
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        User user = new User();
//        user.setUsername(ownerUsername);
//        existingPost.setUser(user);
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(username);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Act and Assert
//        assertThrows(AccessDeniedException.class, () -> postService.deletePost(postId));
//
//        verify(postRepository, never()).delete(existingPost);
//    }
//    @Test
//    public void testDeletePost_WithInvalidPostId_ThrowsIllegalArgumentException() {
//        // Arrange
//        Long postId = 1L;
//
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> postService.deletePost(postId));
//
//        verify(postRepository, never()).delete(any(Post.class));
//    }
//
//
//    // Test editPost method
//    @Test
//    public void testEditPost() {
//        Long postId = 1L;
//        String currentUser = "Test Username";
//        String content = "Test new content";
//
//        PostRequest postRequest = new PostRequest();
//        postRequest.setContent(content);
//
//        Principal principal = mock(Principal.class);
//        when(principal.getName()).thenReturn(currentUser);
//
//        Post existPost = new Post();
//        existPost.setId(postId);
//        existPost.setContent("Old content");
//
//        User user = new User();
//        user.setUsername(currentUser);
//        existPost.setUser(user);
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(existPost));
//        when(postRepository.save(existPost)).thenReturn(existPost);
//
//        // Action
//        Post editedPost =  postService.editPost(postId, postRequest, principal);
//
//        // Assert
//        assertNotNull(editedPost);
//        assertEquals(content, editedPost.getContent());
//
//        verify(postRepository, times(1)).findById(postId);
//        verify(postRepository, times(1)).save(existPost);
//    }
//    @Test
//    public void testEditPost_InvalidPostId() {
//        // Arrange
//        Long postId = 1L;
//        PostRequest postUpdateRequest = new PostRequest();
//        Principal principal = mock(Principal.class);
//
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> postService.editPost(postId, postUpdateRequest, principal));
//        verify(postRepository, times(1)).findById(postId);
//        verify(postRepository, never()).save(any(Post.class));
//    }
//    @Test
//    public void testEditPost_WithUnauthorizedUser_ThrowsAccessDeniedException() {
//        // Arrange
//        Long postId = 1L;
//        String currentUser = "user1";
//        String unauthorizedUser = "user2";
//        String newContent = "Updated content";
//
//        // Tạo một đối tượng Post đã tồn tại với người dùng không phải là currentUser
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        existingPost.setContent("Old content");
//        User postUser = new User();
//        postUser.setUsername(unauthorizedUser);
//        existingPost.setUser(postUser);
//
//        // Tạo một đối tượng PostRequest với nội dung mới
//        PostRequest postUpdateRequest = new PostRequest();
//        postUpdateRequest.setContent(newContent);
//
//        // Thiết lập giả thiết cho phương thức findById() của postRepository để trả về Optional chứa đối tượng existingPost
//        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//
//        // Act và Assert
//        assertThrows(AccessDeniedException.class, () -> postService.editPost(postId, postUpdateRequest, createPrincipal(currentUser)));
//
//        verify(postRepository, times(1)).findById(postId); // Kiểm tra xem phương thức findById() đã được gọi đúngSố lần với tham số đúng
//        verify(postRepository, never()).save(any(Post.class)); // Đảm bảo rằng phương thức save() không được gọi
//    }
//    private Principal createPrincipal(String username) {
//        return new Principal() {
//            @Override
//            public String getName() {
//                return username;
//            }
//        };
//    }
//
//
//}
