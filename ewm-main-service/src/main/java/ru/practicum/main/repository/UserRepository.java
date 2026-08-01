package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    @Query(value = "SELECT * FROM users ORDER BY id ASC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);
}
