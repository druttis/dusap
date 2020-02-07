package org.dru.dusap.rpc;

public interface RpcClientManager {
    void register(String protocol, RpcClientFactory factory);

    RpcClient getClient(String url);
}
