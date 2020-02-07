package org.dru.dusap.rpc.event;

import org.dru.dusap.rpc.RpcToken;

import java.util.Objects;

public final class RpcErrorEvent<T> extends RpcEvent<T> {
    private final Error error;

    public RpcErrorEvent(final RpcToken<T> source, final Error error) {
        super(source);
        this.error = Objects.requireNonNull(error, "error");
    }

    public Error getError() {
        return error;
    }
}
