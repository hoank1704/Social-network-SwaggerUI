package com.springboot.repository;

import com.springboot.dto.CommentDTO2;
import com.springboot.entities.Comment;
import com.springboot.entities.Post;
import com.springboot.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Xóa comment
    void delete(Comment comment);

    List<Comment> findByUserId(Long userId);

    List<Comment> findByUserIdAndDeleted(Long userId, boolean deleted);

    // Đếm cmt theo post
    int countByPost(Post post);

    // Đếm comment theo postId
    long countByPostId(Long postId);

    Optional<Comment> findById(Long commentId);

    //@Query("SELECT new com.springboot.dto.CommentDTO2(c.id, c.content, c.createdDate, c.user.id) FROM Comment c WHERE c.post.id = :postId")
    @Query("SELECT new com.springboot.dto.CommentDTO2(c.id, c.content, c.createdDate, c.user.id) FROM Comment c WHERE c.post.id = :postId AND c.deleted = false")
    Page<CommentDTO2> findByPostId(Long postId, Pageable pageable);

}
