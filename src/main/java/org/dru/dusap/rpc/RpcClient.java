package org.dru.dusap.rpc;

public interface RpcClient {
    <T> RpcToken<T> request(String method, Class<T> returnType, Object... params);

    RpcToken<?> notify(String method, Object... params);
}
