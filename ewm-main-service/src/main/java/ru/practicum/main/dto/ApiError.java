package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }
}