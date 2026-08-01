package ru.practicum.main.service.user;

import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids);

    List<UserDto> getUsers(int from, int size);

    UserDto createUser(NewUserRequest dto);

    void deleteUser(Long id);
}
