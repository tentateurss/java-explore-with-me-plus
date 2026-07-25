package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.practicum.stats.client.exceptions.StatsClientRequestException;
import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.dto.ErrorResponse;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.client.exceptions.StatsServerException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StatsClient {

    private final RestClient client;
    private final ObjectMapper objectMapper;

    public StatsClient(@Value("${stats-server.url}") String baseUrl,
                       final RestClient.Builder builder,
                       final ObjectMapper objectMapper) {
        this.client = builder
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Void> saveHit(String app, String uri, String ip, LocalDateTime timestamp) {
        log.info("stats-client: Получен запрос на сохранение данных о запросе.");
        log.trace("stats-client: Данные запроса: app={}, uri={}, ip={}, timestamp={}.", app, uri, ip, timestamp);

        HitRequestDto hit = HitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        return client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hit)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        throwCustomServerException(response)))
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        throwCustomClientRequestException(response)))
                .toBodilessEntity();
    }

    public ResponseEntity<List<ViewStats>> getStats(String start, String end,
                                                    @Nullable String[] uris, @Nullable Boolean unique) {

        log.info("stats-client: Получен запрос на получение статистики по посещениям.");
        log.trace("stats-client: Детали запроса: start={}, end={}, uris={}, unique={}.", start, end,
                uris != null ? Arrays.toString(uris) : "null",
                unique != null ? unique : "null");
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParamIfPresent("uris",
                                Optional.ofNullable(uris).filter(array -> array.length > 0))
                        .queryParamIfPresent("unique", Optional.ofNullable(unique))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        throwCustomServerException(response)))
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        throwCustomClientRequestException(response)))
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private void throwCustomServerException(ClientHttpResponse response) {
        try {
            ErrorResponse resp = objectMapper.readValue(response.getBody(), ErrorResponse.class);
            String error = resp.getMessage();
            throw new StatsServerException(String.format("Ошибка сервера статистики: %s.",
                    error != null ? error : "детали неизестны"));
        } catch (IOException e) {
            log.error("stats-client: не удалось прочитать сообщение об ошибке. Код ошибки: 5хх.");
            throw new StatsServerException("Неизвестная ошибка сервера статистики.");
        }
    }

    private void throwCustomClientRequestException(ClientHttpResponse response) {
        try {
            ErrorResponse resp = objectMapper.readValue(response.getBody(), ErrorResponse.class);
            String error = resp.getMessage();
            throw new StatsClientRequestException(String.format("Ошибка обработки запроса: %s.",
                    error != null ? error : "детали неизестны"));
        } catch (IOException e) {
            log.error("stats-client: не удалось прочитать сообщение об ошибке. Код ошибки: 4хх.");
            throw new StatsClientRequestException("Неизвестная ошибка обработки запроса к серверу статистики.");
        }
    }

}
