package com.springboot.repository;

import com.springboot.entities.Friendship;
import com.springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUser1AndUser2(User user1, User user2);

    List<Friendship> findByUser1AndStatus(User user, String status);
    List<Friendship> findByUser2AndStatus(User user, String status);

    List<Friendship> findByUser1OrUser2AndStatus(User user1, User user2, String status);
    Optional<Friendship> findByUser1AndUser2AndStatus(User user1, User user2, String status);
    Friendship findByUser1AndUser2(User user1, User user2);

}
