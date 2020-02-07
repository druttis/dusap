package org.dru.dusap.rpc.event;

import org.dru.dusap.rpc.RpcToken;

import java.util.Objects;

public final class RpcResultEvent<T> extends RpcEvent<T> {
    private T result;

    public RpcResultEvent(final RpcToken<T> source, final T result) {
        super(source);
        this.result = Objects.requireNonNull(result, "result");
    }

    public T getResult() {
        return result;
    }
}
