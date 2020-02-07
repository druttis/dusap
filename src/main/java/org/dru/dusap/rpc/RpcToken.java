package org.dru.dusap.rpc;

import org.dru.dusap.event.EventDispatcher;

import java.time.Duration;

public interface RpcToken<T> extends EventDispatcher {
    String getMethod();

    Object[] getParams();

    Class<T> getReturnType();

    T get() throws RpcError;

    T get(Duration timeout) throws RpcError;
}
