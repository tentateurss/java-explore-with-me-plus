package ru.practicum.main.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private static final NewUserRequest TEST_USER = new NewUserRequest("name", "name@email.com");

    private final UserService service;
    private final UserRepository repository;
    private final EntityManager em;

    @Test
    public void testGetUsersByIds_returnsEmptyList_whenNoUsersFound() {
        // testing when no users exist
        assertTrue(service.getUsers(List.of(1L)).isEmpty());

        long id = service.createUser(TEST_USER).getId();

        // testing when users with different ids exist
        List<UserDto> result = service.getUsers(List.of(id + 300L));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersById_shouldReturnUsers_WithIdsInList() {
        long id = service.createUser(TEST_USER).getId();

        List<UserDto> result = service.getUsers(List.of(id));

        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(id));
        assertThat(result.getFirst().getName(), is(TEST_USER.getName()));
        assertThat(result.getFirst().getEmail(), is(TEST_USER.getEmail()));
    }

    @Test
    public void testGetUsers_ByOffsetAndLimit_returnsUsersWithOffsetAndLimit() {
        long id1 = service.createUser(TEST_USER).getId();
        long id2 = service.createUser(new NewUserRequest("test", "different@email.com")).getId();
        long id3 = service.createUser(new NewUserRequest("abc", "def@g.hi")).getId();

        List<UserDto> result = service.getUsers(1, 2);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().noneMatch(dto -> dto.getId() == id1));
        assertTrue(result.stream().allMatch(dto -> dto.getId() == id2 || dto.getId() == id3));
    }

    @Test
    public void testGetUsers_ByOffsetAndLimit_returnsEmptyList_whenNoUsersExist() {
        // testing when no users exist in database
        assertTrue(service.getUsers(0, 10).isEmpty());

        // testing when users exist, but none satisfy the offset condition
        service.createUser(TEST_USER);
        assertTrue(service.getUsers(10, 10).isEmpty());
    }

    @Test
    public void testCreateUser_shouldCreateUser() {
        long id = service.createUser(TEST_USER).getId();

        TypedQuery<User> query = em.createQuery("select u from User u where id = :id", User.class);
        User user = query.setParameter("id", id).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), is(TEST_USER.getName()));
        assertThat(user.getEmail(), is(TEST_USER.getEmail()));
    }

    @Test
    public void testCreateUser_shouldCreateUser_withNameTrimmed() {
        NewUserRequest dto = new NewUserRequest("name    ", TEST_USER.getEmail());

        long id = service.createUser(dto).getId();

        TypedQuery<User> query = em.createQuery("select u from User u where id = :id", User.class);
        User user = query.setParameter("id", id).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), is(dto.getName().trim()));
    }

    @Test
    public void testCreateUser_shouldThrow_ifEmailNotUnique() {
        service.createUser(TEST_USER);

        assertThrows(ConflictException.class,
                () -> service.createUser(new NewUserRequest("different user", TEST_USER.getEmail())));
    }

    @Test
    public void testDeleteUser_shouldDeleteUser() {
        long id = service.createUser(TEST_USER).getId();

        service.deleteUser(id);

        assertThat(service.getUsers(List.of(id)), empty());
    }

    @Test
    public void testDeleteUser_shouldThrow_ifUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.deleteUser(9999L));
    }
}
