package org.dru.dusap.fx;

import javafx.application.Platform;
import org.dru.dusap.event.DirectEventDelegator;
import org.dru.dusap.event.EventDelegator;

import java.util.List;
import java.util.function.Consumer;

public enum FXEventDispatcher implements EventDelegator {
    INSTANCE;

    @Override
    public <E> void delegateEventToListenerList(final E event, final List<Consumer<E>> listeners) {
        Platform.runLater(() -> DirectEventDelegator.INSTANCE.delegateEventToListenerList(event, listeners));
    }
}
