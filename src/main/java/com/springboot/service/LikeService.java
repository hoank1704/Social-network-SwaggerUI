package com.springboot.service;

import com.springboot.entities.Like;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import com.springboot.dto.LikeDTO;
import com.springboot.repository.LikeRepository;
import com.springboot.repository.PostRepository;
import com.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class LikeService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;


    public void likePost(LikeDTO likeDTO, Principal principal) {
        String currentUsername = principal.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);
        Long postId = likeDTO.getPostId();
        Post post = postRepository.findById(postId).orElse(null);

        if (user != null && post != null) {
            Like existingLike = likeRepository.findByUserAndPost(user, post);

            if (existingLike == null) {
                Like like = new Like();
                like.setUser(user);
                like.setPost(post);
                likeRepository.save(like);
            } else {
                throw new IllegalArgumentException("User has already liked this post");
            }
        } else {
            throw new IllegalArgumentException("Invalid user or post");
        }
    }

    public void unlikePost(Long postId, Principal principal) {
        String currentUsername = principal.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);
        Post post = postRepository.findById(postId).orElse(null);

        if (user != null && post != null) {
            Like like = likeRepository.findByUserAndPost(user, post);
            if (like != null) {
                likeRepository.delete(like);
            } else {
                throw new IllegalArgumentException("User has unliked this post");
            }
        } else {
            throw new IllegalArgumentException("Invalid user or post");
        }
    }

}

