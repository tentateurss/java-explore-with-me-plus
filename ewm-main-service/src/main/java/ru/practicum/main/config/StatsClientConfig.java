package ru.practicum.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.practicum.stats.client.StatsClient;

@Configuration
public class StatsClientConfig {

    @Bean
    public StatsClient statsClient(
            @Value("${stats-server.url}") String baseUrl,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper) {
        return new StatsClient(baseUrl, restClientBuilder, objectMapper);
    }
}