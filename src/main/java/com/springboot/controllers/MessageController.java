//package com.springboot.controllers;
//
//import com.springboot.dto.MessageDTO;
//import com.springboot.dto.ReceivedMessageDTO;
//import com.springboot.dto.UserDTO;
//import com.springboot.entities.Message;
//import com.springboot.entities.User;
//import com.springboot.repository.MessageRepository;
//import com.springboot.repository.UserRepository;
//import com.springboot.service.FriendshipService;
//import com.springboot.service.MessageService;
//import com.springboot.service.UserService;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/messages")
//public class MessageController {
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private FriendshipService friendshipService;
//    @Autowired
//    private MessageService messageService;
//    @Autowired
//    private MessageRepository messageRepository;
//
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public MessageController(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//
//    @SecurityRequirement(name = "Bearer Authentication")
//    @PostMapping
//    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
//        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
//
//        // Xử lý tin nhắn
//        User sender = messageService.getUserById(currentUserId);
//        User recipient = messageService.getUserById(messageDTO.getRecipientId());
//
//        if ((sender != null && recipient != null) || (friendshipService.areFriends(sender.getId(), recipient.getId()))) {
//            Message message = new Message();
//            message.setSender(sender);
//            message.setRecipient(recipient);
//            message.setContent(messageDTO.getContent());
//            message.setSentAt(LocalDateTime.now());
//            messageRepository.save(message);
//
//            // Gửi tin nhắn đến recipient thông qua WebSocket
//            messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/queue/messages", message);
//
//            return ResponseEntity.ok().build();
//        } else {
//            throw new IllegalArgumentException("Invalid sender, recipient, or friendship status.");
//        }
//    }
//
////    @SecurityRequirement(name = "Bearer Authentication")
////    @PostMapping
////    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
////        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
////        //messageDTO.setSenderId(currentUserId);
////        messageService.sendMessage(messageDTO, currentUserId);
////        return ResponseEntity.ok().build();
////    }
////
////    @SecurityRequirement(name = "Bearer Authentication")
////    @GetMapping("/received-messages")
////    public ResponseEntity<List<ReceivedMessageDTO>> getReceivedMessages(
////            @RequestParam(defaultValue = "0") int page,
////            @RequestParam(defaultValue = "10") int size,
////            @AuthenticationPrincipal UserDetails userDetails) {
////
////        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
////        List<Message> receivedMessages = messageRepository.findByRecipientId(currentUserId);
////
////        List<ReceivedMessageDTO> receivedMessageDTOs = receivedMessages.stream()
////                .map(message -> {
////                    ReceivedMessageDTO dto = new ReceivedMessageDTO();
////                    dto.setMessageId(message.getId());
////                    dto.setSenderId(message.getSender().getId());
////                    dto.setSenderUsername(message.getSender().getUsername());
////                    dto.setContent(message.getContent());
////                    return dto;
////                })
////                .collect(Collectors.toList());
////
////        return ResponseEntity.ok(receivedMessageDTOs);
////    }
////
////    @SecurityRequirement(name = "Bearer Authentication")
////    @GetMapping("/{userId1}/{userId2}")
////    public ResponseEntity<?> getMessagesBetweenUsers(@PathVariable Long userId1, @PathVariable Long userId2) {
////        List<Message> messages = messageService.getMessagesBetweenUsers(userId1, userId2);
////        return ResponseEntity.ok(messages);
////    }
//}
