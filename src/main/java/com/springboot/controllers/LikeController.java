package com.springboot.controllers;

import com.springboot.dto.LikeDTO;
import com.springboot.repository.PostRepository;
import com.springboot.service.LikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/like")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private PostRepository postRepository;

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Principal principal) {
        try {
            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setPostId(postId);
            likeService.likePost(likeDTO, principal);
            return ResponseEntity.ok("Đã like");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Bạn đã like post này rồi");
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Principal principal) {
        try {
            likeService.unlikePost(postId, principal);
            return ResponseEntity.ok("Đã unlike");
        } catch (IllegalArgumentException  e) {
            return ResponseEntity.badRequest().body("Bạn đã unlike post này rồi");
        }
    }
}
