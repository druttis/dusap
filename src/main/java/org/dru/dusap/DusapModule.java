package org.dru.dusap;

import org.dru.dusap.concurrent.ConcurrentModule;
import org.dru.dusap.database.DatabaseModule;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Module;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.rpc.RpcModule;
import org.dru.dusap.rpc.json.JsonRpcModule;
import org.dru.dusap.time.TimeModule;

@DependsOn({
        ConcurrentModule.class,
        DatabaseModule.class,
        JsonModule.class,
        JsonRpcModule.class,
        RpcModule.class,
        TimeModule.class
})
public final class DusapModule extends Module {
    public DusapModule() {
    }

    @Override
    protected void configure() {
        inherit();
    }
}
