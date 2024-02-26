package com.springboot.service;

import com.springboot.dto.MessageDTO;
import com.springboot.entities.Friendship;
import com.springboot.entities.Message;
import com.springboot.entities.User;
import com.springboot.repository.FriendshipRepository;
import com.springboot.repository.MessageRepository;
import com.springboot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserService userService;

    public void sendMessage(MessageDTO messageDTO, Long id) {
        User sender = getUserById(id);
        User recipient = getUserById(messageDTO.getRecipientId());

        if ((sender != null && recipient != null) || (areFriends(sender, recipient))) {
            Message message = new Message();
            message.setSender(sender);
            message.setRecipient(recipient);
            message.setContent(messageDTO.getContent());
            message.setSentAt(LocalDateTime.now());
            messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Invalid sender, recipient, or friendship status.");
        }
    }
    public List<Message> getReceivedMessages(Long userId) {
        //Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("createdAt").descending());
        return messageRepository.findByRecipientId(userId);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found userId: " + id));
    }


    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        User user1 = getUserById(userId1);
        User user2 = getUserById(userId2);

        if (user1 != null && user2 != null && areFriends(user1, user2)) {
            LocalDateTime since = LocalDateTime.now().minusDays(69);
            return messageRepository.findBySenderAndRecipientAndSentAtAfter(user1, user2, since);
        } else {
            throw new IllegalArgumentException("Invalid user IDs or friendship status.");
        }
    }

    private boolean areFriends(User user1, User user2) {
        Friendship friendship = friendshipRepository.findByUser1AndUser2(user1, user2);
        return friendship != null && friendship.getStatus().equals("accepted");
    }
}
