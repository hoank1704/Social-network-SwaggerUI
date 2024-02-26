package com.springboot.repository;

import com.springboot.entities.Post;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EntityScan("com.springboot.entities")
public interface PostRepository extends JpaRepository<Post, Long> {

    // Timeline
//    @Query(value = "SELECT p.user_id, p.id, p.content, p.created_at\n" +
//                    "FROM post AS p\n" +
//                    "WHERE p.user_id IN " +
//                        "(SELECT f.user_id_1 FROM friendship AS f WHERE f.user_id_2 = ?1 AND f.status = 'accepted'\n" +
//                    "UNION ALL\n" +
//                        "SELECT f.user_id_2 FROM friendship AS f WHERE f.user_id_1 = ?1 AND f.status = 'accepted')\n" +
//                    "ORDER BY p.created_at DESC", nativeQuery = true)
    @Query(value = "SELECT p.user_id, p.id, p.content, p.created_at\n" +
            "FROM post AS p\n" +
            "WHERE (p.user_id IN " +
            "(SELECT f.user_id_1 FROM friendship AS f WHERE f.user_id_2 = ?1 AND f.status = 'accepted'\n" +
            "UNION ALL\n" +
            "SELECT f.user_id_2 FROM friendship AS f WHERE f.user_id_1 = ?1 AND f.status = 'accepted')\n" +
            "OR p.user_id = ?1)\n" +
            "ORDER BY p.created_at DESC", nativeQuery = true)
    Page<Post> findFriendPostsByUserId(Long userId, Pageable pageable);

    List<Post> findByUserId(Long userId);

    Optional<Post> findById(Long id);

}
