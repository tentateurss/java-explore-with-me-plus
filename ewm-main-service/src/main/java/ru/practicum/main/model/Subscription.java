package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;  // кто подписывается

    @Column(name = "author_id", nullable = false)
    private Long authorId;      // на кого подписываются

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
