package com.springboot.service;

import com.springboot.entities.Image;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import com.springboot.dto.PostDTO;
import com.springboot.payload.request.PostRequest;
import com.springboot.payload.response.ImagePostResponse;
import com.springboot.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;

    // Tạo post
    public Post createPost(PostRequest postRequest, List<String> imageUrls, String username) {
        // Tạo một đối tượng bài đăng mới
        Post post = new Post();
        post.setContent(postRequest.getContent());

        // Gán các URL của ảnh
        List<Image> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setUrl(imageUrl);
            images.add(image);
        }
        post.setImages(images);

        // Lấy thông tin user
        User user = userRepository.findByUsername(username).orElse(null);
        post.setUser(user);

        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        for (Image image : images) {
            image.setPost(savedPost);
            imageRepository.save(image);
        }

        return savedPost;
    }

    // Get post By UserId
    public List<Post> getPostByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }


    public ImagePostResponse getImageByPostId(Long postId, Long imageId) throws Exception {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            List<Image> images = post.getImages();
            if (images.isEmpty()) {
                throw new NotFoundException("No images found for the post with id: " + postId);
            }

            Image image = images.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Image not found with id: " + imageId));

            String imageUrl = image.getUrl();

            try {
                ByteArrayResource imageResource = imageService.getImage(imageUrl);
                byte[] imageBytes = imageResource.getInputStream().readAllBytes();
                return new ImagePostResponse(imageBytes, imageId.toString());
            } catch (IOException e) {
                throw new Exception("Failed to retrieve the image for the post with id: " + postId, e);
            }
        }
        throw new NotFoundException("Post not found with id: " + postId);
    }

    // Get Post theo postId, lấy được số lượng comment và userId trong post đó
    public PostDTO getPostsById(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            User user = post.getUser();
            int commentCount = commentRepository.countByPost(post);
            int likeCount = likeRepository.countByPostId(postId);

            // Chuyển đổi danh sách ảnh thành chuỗi tên ảnh
            List<String> imageNames = post.getImages().stream()
                    .map(Image::getUrl)
                    .collect(Collectors.toList());
            String imageNamesString = String.join(", ", imageNames);

            LocalDateTime createdAt = post.getCreatedAt().toLocalDate().atStartOfDay();

            return new PostDTO(post.getContent(), createdAt, commentCount, likeCount, user.getId(), imageNamesString);
        }
        return null;
    }

    @Transactional
    public Post editPost(Long postId, PostRequest postUpdateRequest, Principal principal) {
        // Lấy bài đăng dựa trên ID
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // Kiểm tra xem user có quyền sửa post không
        String currentUser = principal.getName();
        if (!existingPost.getUser().getUsername().equals(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền sửa bài đăng này.");
        }

        existingPost.setContent(postUpdateRequest.getContent());
        return postRepository.save(existingPost);
    }

    // Lấy Post theo Id để thực hiện delete Post
    public Post getPostByIdToDelete(Long postId) {
        // Lấy bài đăng dựa trên ID bài đăng
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Bài đăng không tồn tại."));
    }

    @Transactional
    public void deletePost(Long postId) {
        // Lấy bài đăng dựa trên ID
        Post existingPost = getPostByIdToDelete(postId);
        if (!existingPost.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AccessDeniedException("Bạn không có quyền xóa bài đăng này.");
        }
        postRepository.delete(existingPost);
    }
}
