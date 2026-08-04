package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Исправлено: requestorId вместо requesterId
    List<ParticipationRequest> findAllByRequestorId(Long userId);

    // Исправлено: requestorId вместо requesterId
    boolean existsByRequestorIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    ParticipationRequest findByIdAndRequestorId(Long requestId, Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);
}