package ru.practicum.main.enums;

import lombok.Getter;

@Getter
public enum EventStateAdminAction {
    PUBLISH_EVENT(EventState.PUBLISHED),
    REJECT_EVENT(EventState.CANCELED);

    private final EventState associatedState;

    EventStateAdminAction(EventState associatedState) {
        this.associatedState = associatedState;
    }
}