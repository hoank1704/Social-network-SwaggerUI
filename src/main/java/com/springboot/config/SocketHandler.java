//package com.springboot.config;
//
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Component
//public class SocketHandler extends TextWebSocketHandler {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public SocketHandler(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // Xử lý tin nhắn nhận được từ client
//        String payload = message.getPayload();
//        // Gửi tin nhắn đến tất cả các client đã kết nối
//        messagingTemplate.convertAndSend("/topic/messages", payload);
//    }
//}
//
