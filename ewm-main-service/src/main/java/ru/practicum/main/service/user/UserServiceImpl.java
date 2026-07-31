package ru.practicum.main.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.debug("main-service - UserServiceImpl: Получен запрос на получение списка пользователей.");
        log.trace("main-service - UserServiceImpl: Подробности запроса на получение списка пользователей: " +
                "ids={}, from={}, size={}.", ids, from, size);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<User> result = ids == null ?
                repository.findAll(PageRequest.of(from / size, size)).toList() :
                repository.findAllById(ids);

        return result.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest dto) {
        log.debug("main-service - UserServiceImpl: Получен запрос на добавление нового пользователя.");
        log.trace("main-service - UserServiceImpl: Подробности запроса на добавление пользователя: {}.", dto);

        if (repository.existsByEmailIgnoreCase(dto.getEmail().trim())) {
            log.warn("main-service - UserServiceImpl: " +
                    "Ошибка при создании пользователя. Пользователь с email={} уже существует.", dto.getEmail());
            throw new ConflictException(String.format("Пользователем с email [%s] уже существует.", dto.getEmail()));
        }

        User user = UserMapper.toEntity(dto);
        user = repository.save(user);

        log.info("main-service - UserServiceImpl: Пользователь [id={}] успешно сохранен.", user.getId());

        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        log.debug("main-service - UserServiceImpl: Получен запрос на удаление пользователя [id={}].", id);

        if (!repository.existsById(id)) {
            log.warn("main-service - UserServiceImpl: Пользователь [id={}] не найден.", id);
            throw new NotFoundException(String.format("Пользователь [id=%s] не найден.", id));
        }

        repository.deleteById(id);

        log.info("Пользователь [id={}] успешно удален.", id);
    }
}
