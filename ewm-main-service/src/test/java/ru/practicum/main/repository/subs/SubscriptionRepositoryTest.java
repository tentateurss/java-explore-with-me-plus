package ru.practicum.main.repository.subs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.main.model.Subscription;
import ru.practicum.main.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Subscription subscription1;
    private Subscription subscription2;
    private Subscription subscription3;

    @BeforeEach
    void setUp() {
        subscription1 = new Subscription();
        subscription1.setSubscriberId(1L);
        subscription1.setAuthorId(2L);
        subscription1.setCreatedAt(LocalDateTime.now());

        subscription2 = new Subscription();
        subscription2.setSubscriberId(1L);
        subscription2.setAuthorId(3L);
        subscription2.setCreatedAt(LocalDateTime.now());

        subscription3 = new Subscription();
        subscription3.setSubscriberId(3L);
        subscription3.setAuthorId(1L);
        subscription3.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.saveAll(List.of(subscription1, subscription2, subscription3));
    }

    @Test
    void existsBySubscriberIdAndAuthorId_shouldReturnTrue_whenSubscriptionExists() {
        boolean exists = subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L);
        assertThat(exists).isTrue();
    }

    @Test
    void existsBySubscriberIdAndAuthorId_shouldReturnFalse_whenSubscriptionNotExists() {
        boolean exists = subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 99L);
        assertThat(exists).isFalse();
    }

    @Test
    void findBySubscriberId_shouldReturnAllSubscriptions_whenUserHasSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(1L);
        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions).extracting("authorId").containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void findBySubscriberId_shouldReturnEmptyList_whenUserHasNoSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(99L);
        assertThat(subscriptions).isEmpty();
    }

    @Test
    void findByAuthorId_shouldReturnAllSubscribers_whenUserHasSubscribers() {
        List<Subscription> subscribers = subscriptionRepository.findByAuthorId(1L);
        assertThat(subscribers).hasSize(1);
        assertThat(subscribers.get(0).getSubscriberId()).isEqualTo(3L);
    }

    @Test
    void findByAuthorId_shouldReturnEmptyList_whenUserHasNoSubscribers() {
        List<Subscription> subscribers = subscriptionRepository.findByAuthorId(99L);
        assertThat(subscribers).isEmpty();
    }

    @Test
    void deleteBySubscriberIdAndAuthorId_shouldDeleteSubscription() {
        subscriptionRepository.deleteBySubscriberIdAndAuthorId(1L, 2L);

        boolean exists = subscriptionRepository.existsBySubscriberIdAndAuthorId(1L, 2L);
        assertThat(exists).isFalse();

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(1L);
        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getAuthorId()).isEqualTo(3L);
    }

    @Test
    void deleteAllBySubscriberId_shouldDeleteAllSubscriptions_whenUserIsSubscriber() {
        subscriptionRepository.deleteAllBySubscriberId(1L);

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(1L);
        assertThat(subscriptions).isEmpty();

        List<Subscription> subscribers = subscriptionRepository.findByAuthorId(1L);
        assertThat(subscribers).hasSize(1);
    }

    @Test
    void deleteAllByAuthorId_shouldDeleteAllSubscriptions_whenUserIsAuthor() {
        subscriptionRepository.deleteAllByAuthorId(1L);

        List<Subscription> subscribers = subscriptionRepository.findByAuthorId(1L);
        assertThat(subscribers).isEmpty();

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(1L);
        assertThat(subscriptions).hasSize(2);
    }

    @Test
    void countByAuthorId_shouldReturnCorrectCount() {
        long count = subscriptionRepository.countByAuthorId(1L);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByAuthorId_shouldReturnZero_whenUserHasNoSubscribers() {
        long count = subscriptionRepository.countByAuthorId(99L);
        assertThat(count).isZero();
    }
}