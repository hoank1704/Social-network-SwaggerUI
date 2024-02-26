package com.springboot.service;

import com.springboot.entities.Friendship;
import com.springboot.entities.User;
import com.springboot.repository.FriendshipRepository;
import com.springboot.service.FriendshipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FriendshipServiceTest {
    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testSendFriendRequest() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        Friendship friendship = new Friendship();
        friendship.setUser1(sender);
        friendship.setUser2(receiver);
        friendship.setStatus("pending");

        when(friendshipRepository.existsByUser1AndUser2(sender, receiver)).thenReturn(false);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        // Act
        Friendship result = friendshipService.sendFriendRequest(sender, receiver);

        // Assert
        Assertions.assertEquals(friendship, result);
        verify(friendshipRepository, times(1)).existsByUser1AndUser2(sender, receiver);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void testSendFriendRequest_SenderEqualsReceiver() {
        // Arrange
        User sender = new User();
        sender.setId(1L);

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            friendshipService.sendFriendRequest(sender, sender);
        });
    }

    @Test
    void testSendFriendRequest_FriendRequestExists() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(friendshipRepository.existsByUser1AndUser2(sender, receiver)).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(IllegalStateException.class, () -> {
            friendshipService.sendFriendRequest(sender, receiver);
        });
    }

    @Test
    void testAcceptFriendRequest() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setUser2(currentUser);

        // Act
        friendshipService.acceptFriendRequest(friendship, currentUser);

        // Assert
        Assertions.assertEquals("accepted", friendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testAcceptFriendRequest_UserDoesNotHavePermission() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setUser2(new User());

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            friendshipService.acceptFriendRequest(friendship, currentUser);
        });
        Assertions.assertNotEquals("accepted", friendship.getStatus());
        verify(friendshipRepository, never()).save(friendship);
    }

    @Test
    void testRejectFriendRequest() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setUser2(currentUser);

        // Act
        friendshipService.rejectFriendRequest(friendship, currentUser);

        // Assert
        verify(friendshipRepository, times(1)).delete(friendship);
    }

    @Test
    void testRejectFriendRequest_CannotRejectFriendRequestOfAnotherUser() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setUser2(new User());

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            friendshipService.rejectFriendRequest(friendship, currentUser);
        });
        verify(friendshipRepository, never()).delete(friendship);
    }

    @Test
    void testGetPendingFriendRequests() {
        // Arrange
        User user = new User();
        user.setId(1L);

        List<Friendship> expectedFriendships = new ArrayList<>();
        Friendship friendship1 = new Friendship();
        friendship1.setUser2(user);
        friendship1.setStatus("pending");
        expectedFriendships.add(friendship1);
        Friendship friendship2 = new Friendship();
        friendship2.setUser2(user);
        friendship2.setStatus("accepted");
        expectedFriendships.add(friendship2);

        when(friendshipRepository.findByUser2AndStatus(user, "pending")).thenReturn(expectedFriendships);

        // Act
        List<Friendship> result = friendshipService.getPendingFriendRequests(user);

        // Assert
        Assertions.assertEquals(expectedFriendships, result);
        verify(friendshipRepository, times(1)).findByUser2AndStatus(user, "pending");
    }

    @Test
    void testGetFriends() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Friendship friendship1 = new Friendship();
        friendship1.setUser1(user);
        friendship1.setUser2(new User());
        friendship1.setStatus("accepted");

        Friendship friendship2 = new Friendship();
        friendship2.setUser1(new User());
        friendship2.setUser2(user);
        friendship2.setStatus("accepted");

        List<Friendship> expectedFriendships1 = new ArrayList<>();
        expectedFriendships1.add(friendship1);

        List<Friendship> expectedFriendships2 = new ArrayList<>();
        expectedFriendships2.add(friendship2);

        when(friendshipRepository.findByUser1AndStatus(user, "accepted")).thenReturn(expectedFriendships1);
        when(friendshipRepository.findByUser2AndStatus(user, "accepted")).thenReturn(expectedFriendships2);

        // Act
        List<User> result = friendshipService.getFriends(user);

        // Assert
        Assertions.assertEquals(2, result.size());
        verify(friendshipRepository, times(1)).findByUser1AndStatus(user, "accepted");
        verify(friendshipRepository, times(1)).findByUser2AndStatus(user, "accepted");
    }

    // Đảm bảo rằng bạn đã triển khai setter cho FriendshipRepository trong lớp FriendshipService
    private void setFriendshipRepository(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

}
