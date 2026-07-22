package ru.practicum.stats.client.exceptions;

import java.util.List;

public class StatsClientRequestException extends RuntimeException {
    List<String> errors;

    public StatsClientRequestException(String message) {
        super(message);
    }

    public StatsClientRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
