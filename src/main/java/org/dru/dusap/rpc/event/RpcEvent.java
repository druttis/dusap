package org.dru.dusap.rpc.event;

import org.dru.dusap.rpc.RpcToken;

import java.util.Objects;

public abstract class RpcEvent<T> {
    private final RpcToken<T> source;

    public RpcEvent(final RpcToken<T> source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    public final RpcToken<T> getSource() {
        return source;
    }
}
