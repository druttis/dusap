package org.dru.dusap.rpc;

public class RpcError extends RuntimeException {
    public RpcError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
