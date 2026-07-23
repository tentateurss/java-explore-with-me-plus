package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ru.practicum.stats.client.exceptions.StatsClientRequestException;
import ru.practicum.stats.client.exceptions.StatsServerException;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(StatsClient.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsClientTest {

    private static final int OK = 200;
    private static final int CREATED = 201;

    private final StatsClient client;
    private final MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper;

    @Value("${stats-server.url}")
    private String baseUri;

    @Test
    void testSaveHit() throws Exception {
        EndpointHit hit = EndpointHit.builder()
                .app("app")
                .uri("uri")
                .ip("0.0.0.0")
                .timestamp(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(hit);

        mockServer.expect(requestTo(baseUri + "/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withStatus(HttpStatusCode.valueOf(CREATED)));

        ResponseEntity<Void> response = client.saveHit(hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
        assertThat(response.getBody(), nullValue());
        assertThat(response.getStatusCode(), is(HttpStatusCode.valueOf(CREATED)));
    }

    @Test
    void testGetStats() throws Exception {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(queryParam("start", notNullValue()))
                .andExpect(queryParam("end", notNullValue()))
                .andRespond(withSuccess());

        ResponseEntity<List<ViewStats>> response =
                client.getStats("2020-01-01 10:10:10", "2021-01-01 10:10:10", null, null);

        assertThat(response.getStatusCode(), is(HttpStatusCode.valueOf(OK)));
    }

    @Test
    void testSaveHit_testServerExceptionHandling() {
        mockServer.expect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatusCode.valueOf(500)));
        Assertions.assertThrows(StatsServerException.class,
                () -> client.saveHit("app", "uri", "ip", LocalDateTime.now()));
    }

    @Test
    void testSaveHit_testClientExceptionHandling() {
        mockServer.expect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatusCode.valueOf(400)));
        Assertions.assertThrows(StatsClientRequestException.class,
                () -> client.saveHit("app", "uri", "ip", LocalDateTime.now()));
    }

    @Test
    void testGetStats_testServerExceptionHandling() {
        mockServer.expect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatusCode.valueOf(500)));
        Assertions.assertThrows(StatsServerException.class,
                () -> client.getStats("start", "end", null, null));
    }

    @Test
    void testGetStats_testClientExceptionHandling() {
        mockServer.expect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatusCode.valueOf(400)));
        Assertions.assertThrows(StatsClientRequestException.class,
                () -> client.getStats("start", "end", null, null));
    }
}
