package ru.practicum.main.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main.controller.adminapi.AdminUserController;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.ErrorHandler;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.service.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminUserController.class, ErrorHandler.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminUserControllerTest {
    private static final String USERS_URL = "/admin/users";
    private static final NewUserRequest VALID_NEW_USER = new NewUserRequest("name", "name@email.com");

    @MockBean
    private final UserService userService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @Test
    public void testGetUsers_shouldCallGetUsersById_whenIdsNotNull() throws Exception {
        when(userService.getUsers(any()))
                .thenReturn(List.of());

        mvc.perform(get(USERS_URL)
                        .queryParam("ids", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getUsers(any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetUsers_shouldCallGetUsersWithOffsetAndLimit_whenIdsIsNull() throws Exception {
        when(userService.getUsers(anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(get(USERS_URL)
                        .queryParam("from", "1")
                        .queryParam("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getUsers(1, 5);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetUsers_shouldCallGetsUsers_withDefaultOffsetAndLimit_whenNoQueryParamsPresent() throws Exception {
        when(userService.getUsers(anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(get(USERS_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getUsers(0, 10);
    }

    @Test
    public void testCreateUser_shouldReturnCreated_withValidRequest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(new UserDto(1L, VALID_NEW_USER.getName(), VALID_NEW_USER.getEmail()));

        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(VALID_NEW_USER))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userService).createUser(VALID_NEW_USER);
    }

    @Test
    public void testCreateUser_testConflictException() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(ConflictException.class);

        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(VALID_NEW_USER))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateUser_shouldThrow_ifInvalidName() throws Exception {
        // testing blank name
        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserRequest(" ", "valid@email.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // testing null name
        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserRequest(null, "valid@email.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    public void testCreateUser_shouldThrow_ifInvalidEmail() throws Exception {
        // testing blank email
        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserRequest("valid name", " ")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // testing null email
        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserRequest("valid name", null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // testing invalid email format
        mvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserRequest("valid name", "not an email")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    public void testDeleteUser_onSuccess_returnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mvc.perform(delete(USERS_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    public void testDeleteUser_returnsNotFound_ifUserNotFound() throws Exception {
        doThrow(NotFoundException.class)
                .when(userService).deleteUser(anyLong());

        mvc.perform(delete(USERS_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRuntimeException_returnsInternalServerError() throws Exception {
        doThrow(RuntimeException.class)
                .when(userService).deleteUser(anyLong());

        mvc.perform(delete(USERS_URL + "/1"))
                .andExpect(status().isInternalServerError());
    }
}
