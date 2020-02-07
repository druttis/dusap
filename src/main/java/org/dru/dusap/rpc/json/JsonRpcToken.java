package org.dru.dusap.rpc.json;

import org.dru.dusap.event.EventBus;
import org.dru.dusap.rpc.AbstractRpcToken;
import org.dru.dusap.time.TimeSupplier;

public final class JsonRpcToken<T> extends AbstractRpcToken<T> {
    JsonRpcToken(final EventBus eventBus, final TimeSupplier timeSupplier, final String method,
                 final Object[] params, final Class<T> returnType) {
        super(eventBus, timeSupplier, method, params, returnType);
    }
}
