//package com.springboot.controller;
//
//import com.springboot.controllers.PostController;
//import com.springboot.dto.PostDTO;
//import com.springboot.entities.Post;
//import com.springboot.entities.User;
//import com.springboot.payload.request.PostRequest;
//import com.springboot.payload.response.ImagePostResponse;
//import com.springboot.repository.PostRepository;
//import com.springboot.security.services.UserDetailsImpl;
//import com.springboot.service.ImageService;
//import com.springboot.service.PostService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class PostControllerTest {
//    @Mock
//    private PostService postService;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private ImageService imageService;
//
//    @Mock
//    private UserDetailsImpl userDetails;
//
//    @Mock
//    private Principal principal;
//
//    @Mock
//    private Post post;
//
//    @Mock
//    private Page<Post> postsPage;
//
//    @InjectMocks
//    private PostController postController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGetTimeline() {
//        // Arrange
//        int page = 0;
//        int size = 5;
//
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        SecurityContextHolder.setContext(securityContext); // Thiết lập giá trị cho SecurityContextHolder
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getId()).thenReturn(1L);
//        when(postRepository.findFriendPostsByUserId(1L, PageRequest.of(page, size, Sort.by("created_at").descending()))).thenReturn(postsPage);
//        when(postsPage.getContent()).thenReturn(Collections.singletonList(new Post()));
//
//        // Act
//        ResponseEntity<?> responseEntity = postController.getTimeline(principal, page, size);
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertNotNull(Collections.singletonList(new Post()), String.valueOf(responseEntity.getBody()));
//    }
//
//    @Test
//    public void testCreatePost() throws IOException {
//        // Initialize mocks
//        MockitoAnnotations.openMocks(this);
//
//        // Create test data
//        PostRequest postRequest = new PostRequest();
//        postRequest.setContent("Test Content");
//
//        List<MultipartFile> imageFiles = new ArrayList<>();
//        MultipartFile imageFile = Mockito.mock(MultipartFile.class);
//        imageFiles.add(imageFile);
//
//        Principal principal = Mockito.mock(Principal.class);
//        Mockito.when(principal.getName()).thenReturn("username");
//
//        List<String> imageUrls = new ArrayList<>();
//        imageUrls.add("http://example.com/image.jpg");
//
//        Post post = new Post();
//        post.setId(1L);
//        post.setContent("Test Content");
//        //post.setImages(imageUrls);
//
//        // Configure mock behavior
//        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn("http://example.com/image.jpg");
//        when(postService.createPost(postRequest, imageUrls, principal.getName())).thenReturn(post);
//
//        // Call the method under test
//        ResponseEntity<?> response = postController.createPost(postRequest, imageFiles, principal);
//
//        // Verify the result
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(post, response.getBody());
//    }
//
//    @Test
//    public void testGetPostsByUserId() {
//        // Arrange
//        Long userId = 1L;
//        List<Post> posts = Collections.singletonList(post);
//
//        when(postService.getPostByUserId(userId)).thenReturn(posts);
//
//        // Act
//        ResponseEntity<?> responseEntity = postController.getPostsByUserId(userId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(posts, responseEntity.getBody());
//    }
//
//    @Test
//    public void testGetImageByPostId() throws Exception {
//        // Initialize mocks
//        MockitoAnnotations.openMocks(this);
//
//        // Create test data
//        Long postId = 1L;
//        Long imageId = 1L;
//
//        byte[] imageBytes = {0x12, 0x34, 0x56, 0x78};
//
//        ImagePostResponse image = new ImagePostResponse();
//        image.setImageBytes(imageBytes);
//
//        // Configure mock behavior
//        when(postService.getImageByPostId(postId, imageId)).thenReturn(image);
//
//        // Call the method under test
//        ResponseEntity<byte[]> response = postController.getImageByPostId(postId, imageId);
//
//        // Verify the result
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
//        assertEquals(imageBytes.length, response.getHeaders().getContentLength());
//        assertEquals(imageBytes, response.getBody());
//    }
//
//
//
//
//    @Test
//    public void testGetPostById() {
//        // Arrange
//        Long postId = 1L;
//        PostDTO postDTO = mock(PostDTO.class);
//
//        when(postService.getPostsById(postId)).thenReturn(postDTO);
//
//        // Act
//        ResponseEntity<?> response = postController.getPostById(postId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(postDTO, response.getBody());
//    }
//
//    @Test
//    public void testEditPost_Success() {
//        // Arrange
//        Long postId = 1L;
//        PostRequest postUpdateRequest = mock(PostRequest.class);
//        Principal principal = mock(Principal.class);
//        Post updatedPost = mock(Post.class);
//
//        when(postService.editPost(postId, postUpdateRequest, principal)).thenReturn(updatedPost);
//
//        // Act
//        ResponseEntity<?> response = postController.editPost(postId, postUpdateRequest, principal);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Update thành công", response.getBody());
//    }
//
//    @Test
//    public void testEditPost_Failure() {
//        // Arrange
//        Long postId = 1L;
//        PostRequest postUpdateRequest = mock(PostRequest.class);
//        Principal principal = mock(Principal.class);
//
//        when(postService.editPost(postId, postUpdateRequest, principal)).thenThrow(new IllegalArgumentException("Invalid arguments"));
//
//        // Act
//        ResponseEntity<?> response = postController.editPost(postId, postUpdateRequest, principal);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Invalid arguments", response.getBody());
//    }
//
//    @Test
//    public void testDeletePost() {
//        //Arrange
//        Long postId = 1L;
//        doNothing().when(postService).deletePost(postId);
//
//        // Act
//        ResponseEntity<?> response = postController.deletePost(postId);
//
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//
//    @Test
//    public void editPost_withValidPostId_returnsOkResponse() {
//        // Arrange
//        Long postId = 1L;
//        PostRequest postUpdateRequest = new PostRequest();
//        postUpdateRequest.setContent("Updated content");
//
//        // Create mock objects
//        Principal principal = Mockito.mock(Principal.class);
//        Mockito.when(principal.getName()).thenReturn("username");
//
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        existingPost.setContent("Old content");
//
//        Post updatedPost = new Post();
//        updatedPost.setId(postId);
//        updatedPost.setContent(postUpdateRequest.getContent());
//
//        when(postService.editPost(postId, postUpdateRequest, principal)).thenReturn(updatedPost);
//
//        // Act
//        ResponseEntity<?> response = postController.editPost(postId, postUpdateRequest, principal);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Update thành công", response.getBody());
//        verify(postService, times(1)).editPost(postId, postUpdateRequest, principal);
//    }
//
//
//}
