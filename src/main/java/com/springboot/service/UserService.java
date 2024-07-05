package com.springboot.service;

import com.springboot.dto.UserDTO;
import com.springboot.entities.*;
import com.springboot.payload.request.SignupRequest;
import com.springboot.payload.request.UserUpdateRequest;
import com.springboot.payload.response.AvatarResponse;
import com.springboot.repository.FriendshipRepository;
import com.springboot.repository.ResetTokenRepository;
import com.springboot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.springboot.entities.ERole.ROLE_USER;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ImageService imageService;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    public User findByUsername(String username) {
        // Tìm kiếm user theo username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập: " + username));
    }

    public boolean resetPassword(String newPassword, String username, String token) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Kiểm tra token và xác thực
            ResetToken resetToken = resetTokenRepository.findByToken(token);
            if (null == resetToken || resetToken.isUsed()) {  // nếu không tồn tại hoặc đã được sd
                return false;
            }
            // update trạng thái token đã được sd
            resetToken.setUsed(true);
            resetTokenRepository.save(resetToken);

            // change password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Upload user's avatar
    public User postAvatar(User user, String imageUrl) {
        user.setImageUrl(imageUrl);
        return userRepository.save(user);
    }

    // Get user's avatar
    public AvatarResponse getAvatarUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String avatarUrl = user.getImageUrl();

            byte[] imageBytes;
            if (avatarUrl != null) {
                try {
                    ByteArrayResource imageResource = imageService.getImage(avatarUrl);
                    imageBytes = imageResource.getInputStream().readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    imageBytes = getDefaultAvatar();
                }
            } else {
                imageBytes = getDefaultAvatar();
            }
            return new AvatarResponse(imageBytes);
        }
        return null;
    }

    public byte[] getDefaultAvatar() {
        try {
            ByteArrayResource defaultImageResource = imageService.getImage("logoUser.png");
            return defaultImageResource.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found userId: " + id));

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setImage(user.getImageUrl());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setJob(user.getJob());
        userDTO.setLocation(user.getLocation());

        return userDTO;
    }

    @Transactional
    public User updateUser(UserUpdateRequest userUpdateRequest, User currentUser) {
        currentUser.setUsername(userUpdateRequest.getUsername());
        currentUser.setEmail(userUpdateRequest.getEmail());
        //currentUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        currentUser.setBirthDate(userUpdateRequest.getBirthDate());
        currentUser.setJob(userUpdateRequest.getJob());
        currentUser.setLocation(userUpdateRequest.getLocation());

        return userRepository.save(currentUser);
    }

    @Transactional
    public void deleteUser(User user) {
        // Xóa tất cả các lời mời kết bạn đã gửi
        List<Friendship> sentInvitations = friendshipRepository.findByUser1AndStatus(user, "pending");
        friendshipRepository.deleteAll(sentInvitations);

        // Xóa tất cả các bạn bè hiện tại
        List<Friendship> existingFriendships = friendshipRepository.findByUser1OrUser2AndStatus(user, user, "accepted");
        friendshipRepository.deleteAll(existingFriendships);

        // Xóa user
        userRepository.delete(user);
    }

    public User signUp(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(ROLE_USER);
        userRepository.save(user);
        return user;
    }

}
