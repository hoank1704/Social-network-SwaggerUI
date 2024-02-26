package com.springboot.service;

import com.springboot.entities.Comment;
import com.springboot.entities.User;
import com.springboot.dto.CommentDTO;
import com.springboot.payload.request.CommentUpdateRequest;
import com.springboot.payload.response.CommentNotFoundException;
import com.springboot.payload.response.UnauthorizedException;
import com.springboot.repository.CommentRepository;
import com.springboot.repository.PostRepository;
import com.springboot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    // Add comment
    public CommentDTO createComment(CommentDTO commentDTO, String username) {
        User user = userRepository.findByUsername(username).get();
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setCreatedDate(commentDTO.getCreatedDate());
        comment.setPost(postRepository.findById(commentDTO.getPostId()).get());
        comment.setUser(user);
        Comment createdComment = commentRepository.save(comment);
        CommentDTO createdCommentDTO = new CommentDTO();
        createdCommentDTO.setContent(createdComment.getContent());
        createdCommentDTO.setCreatedDate(createdComment.getCreatedDate());
        createdCommentDTO.setPostId(createdComment.getPost().getId());
        return createdCommentDTO;
    }

    // Edit comment
    @Transactional
    public CommentUpdateRequest updateComment(Long commentId, String newContent, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to update this comment");
        }

        comment.setContent(newContent);
        Comment updatedComment = commentRepository.save(comment);

        CommentUpdateRequest updatedCommentDTO = new CommentUpdateRequest();
        updatedCommentDTO.setContent(updatedComment.getContent());

        return updatedCommentDTO;
    }

    // Soft Delete Comment
    @Transactional
    public void deleteComment(Long commentId, User loggedInUser) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Long postOwnerId = comment.getPost().getUser().getId();
            Long commentOwnerId = comment.getUser().getId();

            if (commentOwnerId == null || (!commentOwnerId.equals(loggedInUser.getId()) && !postOwnerId.equals(loggedInUser.getId()))) {
                throw new UnauthorizedException("User is not allowed to delete this comment");
            }
            comment.setDeleted(true);  // đặt trạng thái thành true
            commentRepository.save(comment);  // Lưu trong CSDL
        } else {
            throw new CommentNotFoundException("Comment not found");
        }
    }

    // get Comments by User Id
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdAndDeleted(userId, false);
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for (Comment comment : comments) {
            commentDTOs.add(new CommentDTO(comment.getContent(), comment.getCreatedDate(), comment.getPost().getId()));
        }
        return commentDTOs;
    }

}

