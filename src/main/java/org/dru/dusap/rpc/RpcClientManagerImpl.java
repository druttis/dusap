package org.dru.dusap.rpc;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class RpcClientManagerImpl implements RpcClientManager {
    private final Map<String, RpcClientFactory> factoryByProtocol;
    private final Map<String, RpcClient> clientByUrl;

    public RpcClientManagerImpl() {
        factoryByProtocol = new ConcurrentHashMap<>();
        clientByUrl = new ConcurrentHashMap<>();
    }

    @Override
    public void register(final String protocol, final RpcClientFactory factory) {
        Objects.requireNonNull(protocol, "protocol");
        Objects.requireNonNull(factory, "factory");
        factoryByProtocol.compute(protocol, ($, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("factory for protocol '" + protocol + "' already registered");
            }
            return factory;
        });
    }

    @Override
    public RpcClient getClient(final String url) {
        Objects.requireNonNull(url, "url");
        final int index = url.indexOf(":");
        final String protocol = url.substring(0, index);
        final String remaining = url.substring(index + 1);
        final RpcClientFactory factory = factoryByProtocol.get(protocol);
        if (factory == null) {
            throw new IllegalArgumentException("no factory registered for protocol '" + protocol + "'");
        }
        return clientByUrl.computeIfAbsent(url, $ -> factory.newClient(remaining));
    }
}
