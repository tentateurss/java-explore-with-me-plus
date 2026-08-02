package ru.practicum.main.service.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.mapper.ParticipationRequestMapper;
import ru.practicum.main.dto.ParticipationRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExist(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private void checkUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}
