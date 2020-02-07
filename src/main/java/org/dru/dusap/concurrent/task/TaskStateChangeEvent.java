package org.dru.dusap.concurrent.task;

import java.util.Objects;

public final class TaskStateChangeEvent {
    private final Task source;

    public TaskStateChangeEvent(final Task source) {
        Objects.requireNonNull(source, "source");
        this.source = source;
    }

    public Task getSource() {
        return source;
    }
}
