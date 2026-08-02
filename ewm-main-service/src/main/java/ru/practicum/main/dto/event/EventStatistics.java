package ru.practicum.main.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Класс-обертка для передачи данных по статистике события:
 * - количество подтвержденных заявок на участие (confirmedRequests);
 * - количество просмотров (views)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatistics {
    private long confirmedRequests;
    private long views;
}
