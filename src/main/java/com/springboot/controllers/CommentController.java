package com.springboot.controllers;

import com.springboot.dto.CommentDTO2;
import com.springboot.entities.Comment;
import com.springboot.entities.User;
import com.springboot.dto.CommentDTO;
import com.springboot.payload.request.CommentUpdateRequest;
import com.springboot.repository.CommentRepository;
import com.springboot.repository.PostRepository;
import com.springboot.repository.UserRepository;
import com.springboot.service.CommentService;
import com.springboot.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    // Create comment
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        try{
            String username = principal.getName();
            commentService.createComment(commentDTO, username);
            return ResponseEntity.ok()
                    .body("Created comment successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Post doesn't exist");
        }
    }

    // Get Comments By PostId
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getCommentsByPostId")
    public ResponseEntity<?> getCommentsByPostId(@RequestParam Long postId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentDTO2> comment = commentRepository.findByPostId(postId, pageable);

            return ResponseEntity.ok(comment.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Post doesn't exist");
        }
    }

    // Delete comment
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                                Principal principal) {
        try {
            User loggedInUser = userService.findByUsername(principal.getName());
            commentService.deleteComment(commentId, loggedInUser);
            return ResponseEntity.ok("Comment soft deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("Comment doesn't exist");
        }
    }

    // update comment by commentId
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/update/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest commentDTO,
            Principal principal) {

        try {
            String username = principal.getName();
            User currentUser = userService.findByUsername(username);

            CommentUpdateRequest updatedComment = commentService.updateComment(commentId, commentDTO.getContent(), currentUser.getId());
            return ResponseEntity.ok(updatedComment);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("Comment does not exist");
        }
    }

    // Get comments by userId
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getComments/{userId}")
    public ResponseEntity<?> getCommentsByUserId(@PathVariable Long userId) {
        try {
            List<CommentDTO> commentDTOs = commentService.getCommentsByUserId(userId);
            return ResponseEntity.ok(commentDTOs);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

}

