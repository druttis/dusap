package org.dru.dusap.rpc;

public interface RpcClientFactory {
    RpcClient newClient(String url);
}
