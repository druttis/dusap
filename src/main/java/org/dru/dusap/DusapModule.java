package org.dru.dusap;

import org.apache.log4j.BasicConfigurator;
import org.dru.dusap.concurrent.ConcurrentModule;
import org.dru.dusap.database.DatabaseModule;
import org.dru.dusap.database.model.DbFactory;
import org.dru.dusap.database.model.DbMember;
import org.dru.dusap.database.model.DbTable;
import org.dru.dusap.injection.*;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.rpc.RpcModule;
import org.dru.dusap.rpc.json.JsonRpcModule;
import org.dru.dusap.time.TimeModule;

import java.awt.*;

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

    @Inject
    protected void test(final DbFactory factory) {
        final DbTable<LiveEvent> table = factory.newTable("LiveEvent", LiveEvent.class);
        table.getMember("type").length(16);
        table.getMember("data").length(1024);
        final DbMember<LiveOpId> id = table.newMember("id", LiveOpId.class).length(32);
        System.out.println(table.getDDL());
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Injector injector = Injection.getInjector(DusapModule.class);
    }

    private static class LiveEvent {
        private String type;
        private String data;

        public LiveEvent(final String type, final String data) {
            this.type = type;
            this.data = data;
        }

        public LiveEvent() {
        }

        public String getType() {
            return type;
        }

        public String getData() {
            return data;
        }
    }

    private static class LiveOpId {
        private String type;
        private long number;

        public LiveOpId(final String type, final long number) {
            this.type = type;
            this.number = number;
        }

        public LiveOpId() {
        }

        public String getType() {
            return type;
        }

        public long getNumber() {
            return number;
        }
    }
}
