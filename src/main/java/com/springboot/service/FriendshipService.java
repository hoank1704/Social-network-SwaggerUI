package com.springboot.service;

import com.springboot.entities.Friendship;
import com.springboot.entities.User;
import com.springboot.repository.FriendshipRepository;
import com.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;

    public boolean areFriends(Long userId1, Long userId2) {
        Optional<User> user1Optional = userRepository.findById(userId1);
        Optional<User> user2Optional = userRepository.findById(userId2);
        if (user1Optional.isPresent() && user2Optional.isPresent()) {
            User user1 = user1Optional.get();
            User user2 = user2Optional.get();
            Optional<Friendship> friendshipOptional = friendshipRepository.findByUser1AndUser2AndStatus(user1, user2, "accepted");
            return friendshipOptional.isPresent();
        }
        return false;
    }

    public Friendship sendFriendRequest(User sender, User receiver) {
        // Check user không thể gửi kết bạn cho chính mình
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        boolean hasSendRequest = friendshipRepository.existsByUser1AndUser2(sender, receiver);
        boolean hasReceivedRequest = friendshipRepository.existsByUser1AndUser2(receiver, sender);
        if (hasSendRequest || hasReceivedRequest) {
            throw new IllegalStateException("Friend request already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setUser1(sender);
        friendship.setUser2(receiver);
        friendship.setStatus("pending");
        return friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(Friendship friendship, User currentUser) {
        if (friendship.getUser2().equals(currentUser)) {
            friendship.setStatus("accepted");
            friendshipRepository.save(friendship);
        } else {
            throw new IllegalArgumentException("User does not have permission to accept this friend request");
        }
    }

    public void rejectFriendRequest(Friendship friendship, User currentUser) {
        if (!friendship.getUser2().equals(currentUser)) {
            throw new IllegalArgumentException("Cannot reject friend request of another user");
        }
        friendshipRepository.delete(friendship);
    }

    public List<Friendship> getPendingFriendRequests(User user) {
        return friendshipRepository.findByUser2AndStatus(user, "pending");
    }

    public List<User> getFriends(User user) {
        List<Friendship> friendships1 = friendshipRepository.findByUser1AndStatus(user, "accepted");
        List<Friendship> friendships2 = friendshipRepository.findByUser2AndStatus(user, "accepted");
        List<User> friends = new ArrayList<>();

        for (Friendship friendship : friendships1) {
            friends.add(friendship.getUser2());
        }

        for (Friendship friendship : friendships2) {
            friends.add(friendship.getUser1());
        }

        return friends;
    }

}

