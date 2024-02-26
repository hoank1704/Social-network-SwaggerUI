package com.springboot.repository;

import com.springboot.entities.Like;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    // Đếm số like trong post
    int countByPostId(Long postId);

    Like findByUserAndPost(User user, Post post);

    List<Like> findByPostId(int postId);

}
