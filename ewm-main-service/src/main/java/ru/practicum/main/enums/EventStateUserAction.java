package ru.practicum.main.enums;

import lombok.Getter;

@Getter
public enum EventStateUserAction {
    SEND_TO_REVIEW(EventState.PENDING),
    CANCEL_REVIEW(EventState.CANCELED);

    private final EventState associatedState;

    EventStateUserAction(EventState associatedState) {
        this.associatedState = associatedState;
    }
}