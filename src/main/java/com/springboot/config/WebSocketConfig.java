//package com.springboot.config;
//
//import org.springframework.boot.autoconfigure.graphql.servlet.GraphQlWebMvcAutoConfiguration.WebSocketConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.support.ExecutorSubscribableChannel;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.*;
//
//import java.io.IOException;
//import java.util.logging.SocketHandler;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        try {
//            registry.addHandler((WebSocketHandler) new SocketHandler(), "/socket").setAllowedOrigins("*");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        registerStompEndpoints((StompEndpointRegistry) registry);
//    }
//
//
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Đăng ký endpoint cho WebSocket
//        registry.addEndpoint("/socket").withSockJS();
//    }
//
//    @Bean
//    public SimpMessagingTemplate messagingTemplate(MessageChannel clientInboundChannel,
//                                                   MessageChannel clientOutboundChannel) {
//        MessageChannel messageChannel = new ExecutorSubscribableChannel();
//        return new SimpMessagingTemplate(messageChannel);
//    }
//}
