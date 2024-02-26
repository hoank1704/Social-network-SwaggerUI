package com.springboot.controllers;

import com.springboot.entities.Friendship;
import com.springboot.entities.User;
import com.springboot.dto.FriendshipDTO;
import com.springboot.dto.UserFriendDTO;
import com.springboot.repository.FriendshipRepository;
import com.springboot.repository.UserRepository;
import com.springboot.security.services.UserDetailsImpl;
import com.springboot.service.FriendshipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    // Send request
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/send-request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long receiverId) {
        try {
            // Lấy thông tin user đã xác thực từ SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // Trả về đối tượng Principal đại diện cho người dùng đã xác thực
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Lấy ID của user gán cho biến loggedInUserId
            long loggedInUserId = userDetails.getId();

            User sender = userRepository.findById(loggedInUserId).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
            User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

            Friendship friendship = friendshipService.sendFriendRequest(sender, receiver);
            //return new FriendshipDTO(friendship.getId(), friendship.getUser1().getId(), friendship.getUser2().getId(), friendship.getStatus());
            return new ResponseEntity<>(new FriendshipDTO(friendship.getId(), friendship.getUser1().getId(), friendship.getUser2().getId(), friendship.getStatus()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send request", HttpStatus.BAD_REQUEST);
        }
    }

    // Accept request
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/accept-request/{friendshipId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long friendshipId, Principal principal) {
        try {
            // Lấy thông tin người dùng hiện tại từ hệ thống xác thực
            String currentUsername = principal.getName();
            User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new EntityNotFoundException("User not found"));

            Friendship friendship = friendshipRepository.findById(friendshipId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid friendship ID"));

            friendshipService.acceptFriendRequest(friendship, currentUser);
            return new ResponseEntity<>("Friend request accepted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Friend request accepted.", HttpStatus.BAD_REQUEST);
        }
    }

    // Reject friend requests and unfriend
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/reject-request/{friendshipId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long friendshipId) {
        try {
            // Lấy thông tin user đã xác thực từ SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // Trả về đối tượng Principal đại diện cho người dùng đã xác thực
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Lấy ID của user gán cho biến loggedInUserId
            long loggedInUserId = userDetails.getId();

            User loggedInUser = userRepository.findById(loggedInUserId).orElseThrow(() -> new IllegalArgumentException("Invalid logged-in user ID"));

            Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new IllegalArgumentException("Invalid friendship ID"));

            friendshipService.rejectFriendRequest(friendship, loggedInUser);
            return new ResponseEntity<>("Xoa ban thanh cong", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to reject request", HttpStatus.BAD_REQUEST);
        }
    }

    // Lấy ra danh sách yêu cầu kết bạn được gửi đến
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/pending-requests")
    public ResponseEntity<?> getPendingFriendRequests(Principal principal) {
        try {
            // Lấy thông tin người dùng hiện tại từ đối tượng Principal
            String username = principal.getName();
            User user = userRepository.findByUsername(username).get();

            List<Friendship> friendships = friendshipService.getPendingFriendRequests(user);
            List<FriendshipDTO> friendshipDTOs = new ArrayList<>();

            for (Friendship friendship : friendships) {
                friendshipDTOs.add(new FriendshipDTO(friendship.getId(), friendship.getUser1().getId(), friendship.getUser2().getId(), friendship.getStatus()));
            }

            return new ResponseEntity<>(friendshipDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot get pending friend request", HttpStatus.BAD_REQUEST);
        }
    }

    // Lấy ra danh sách bạn bè
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/friends/{userId}")
    public ResponseEntity<?> getFriends(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            List<User> friends = friendshipService.getFriends(user);
            List<UserFriendDTO> friendDTOs = new ArrayList<>();
            for (User friend : friends) {
                friendDTOs.add(new UserFriendDTO(friend.getId(), friend.getUsername(), friend.getEmail()));
            }
            return new ResponseEntity<>(friendDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get list of friend", HttpStatus.NOT_FOUND);
        }
    }
}

