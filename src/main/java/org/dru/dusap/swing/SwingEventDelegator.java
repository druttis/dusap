package org.dru.dusap.swing;

import org.dru.dusap.event.DirectEventDelegator;
import org.dru.dusap.event.EventDelegator;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public enum SwingEventDelegator implements EventDelegator {
    INSTANCE;

    @Override
    public <E> void delegateEventToListenerList(final E event, final List<Consumer<E>> listeners) {
        SwingUtilities.invokeLater(() -> DirectEventDelegator.INSTANCE.delegateEventToListenerList(event, listeners));
    }
}
