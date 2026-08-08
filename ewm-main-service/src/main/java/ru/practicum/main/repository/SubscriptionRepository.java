package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.Subscription;

import java.util.List;

//Заглушка
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);

    List<Subscription> findBySubscriberId(Long subscriberId);

    List<Subscription> findByAuthorId(Long authorId);

    void deleteBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);
}
