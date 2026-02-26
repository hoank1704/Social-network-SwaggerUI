package com.springboot.controllers;

import com.springboot.dto.PostDTO;
import com.springboot.entities.Post;
import com.springboot.payload.request.PostRequest;
import com.springboot.payload.response.ImagePostResponse;
import com.springboot.repository.PostRepository;
import com.springboot.repository.UserRepository;
import com.springboot.security.services.UserDetailsImpl;
import com.springboot.service.ImageService;
import com.springboot.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ImageService imageService;

    // Timeline sjhdfgs
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/timeline")
    public ResponseEntity<?> getTimeline(Principal principal,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        // Lấy thông tin xác thực và chi tiết user từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        Page<Post> postsPage = postRepository.findFriendPostsByUserId(userDetails.getId(), pageable);

        return ResponseEntity.ok(postsPage.getContent());

    }

    // Create Post - ok
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/createPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(@ModelAttribute PostRequest postRequest,
                                           @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                           Principal principal) throws IOException {
        try {
            // Thực hiện lưu trữ hình ảnh và nhận URL của hình ảnh
            List<String> imageUrls = new ArrayList<>();

            if (imageFiles != null) {
                for (MultipartFile imageFile : imageFiles) {
                    String imageUrl = imageService.uploadImage(imageFile);
                    imageUrls.add(imageUrl);
                }
            }

            // Tạo bài đăng mới và cập nhật các URL của ảnh (nếu có)
            Post post = postService.createPost(postRequest, imageUrls, principal.getName());
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Create post failed.", HttpStatus.BAD_REQUEST);
        }
    }

    // Get posts by userId
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUserId(@PathVariable Long userId) {
        try {
            List<Post> posts = postService.getPostByUserId(userId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }
    }

    // ok
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getImageByPostId/{postId}/images/{imageId}")
    public ResponseEntity<byte[]> getImageByPostId(@PathVariable Long postId, @PathVariable Long imageId) throws Exception {
        ImagePostResponse image = postService.getImageByPostId(postId, imageId);

        byte[] imageBytes = image.getImageBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getPost/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPostsById(postId);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        }
        return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
    }

    // ok
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/update/{postId}")
    public ResponseEntity<?> editPost(@PathVariable Long postId, @RequestBody PostRequest postUpdateRequest, Principal principal) {
        try {
            Post updatedPost = postService.editPost(postId, postUpdateRequest, principal);
            return ResponseEntity.ok().body("Update thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ok
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        // Xóa bài đăng dựa trên ID bài đăng
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
