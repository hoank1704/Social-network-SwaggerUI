package com.springboot.repository;

import com.springboot.entities.Message;
import com.springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientAndSentAtAfter(User sender, User recipient, LocalDateTime sentAt);

    List<Message> findByRecipientId(Long recipientId);
}
