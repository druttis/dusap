package org.dru.dusap;

import org.apache.log4j.BasicConfigurator;
import org.dru.dusap.concurrent.ConcurrentModule;
import org.dru.dusap.database.DatabaseModule;
import org.dru.dusap.database.model.DbTable;
import org.dru.dusap.database.model.DbTableBuilder;
import org.dru.dusap.database.model.DbTableFactory;
import org.dru.dusap.database.type.AbstractDbType;
import org.dru.dusap.database.type.DbTypes;
import org.dru.dusap.event.EventModule;
import org.dru.dusap.injection.*;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.rpc.RpcClient;
import org.dru.dusap.rpc.RpcClientManager;
import org.dru.dusap.rpc.RpcModule;
import org.dru.dusap.rpc.RpcToken;
import org.dru.dusap.rpc.event.RpcErrorEvent;
import org.dru.dusap.rpc.event.RpcResultEvent;
import org.dru.dusap.rpc.json.JsonRpcModule;
import org.dru.dusap.time.TimeModule;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

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
    private void test(final DbTypes dbTypes, final DbTableFactory dbTableFactory) throws SQLException {
        dbTypes.registerDbType(Instant.class, new DbInstant());
        final DbTableBuilder<LiveOpEvent> builder = dbTableFactory.createBuilder("LiveOpEvent", LiveOpEvent.class);
        builder.setPrimaryKey("key");
        builder.flatten("key", false);
        builder.setLength("key_type", 32);
        builder.flatten("config", false);
        builder.setLength("config_type", 32);
        builder.setLength("config_data", 32768);
        DbTable<LiveOpEvent> liveOpEventTable = builder.build();
        System.out.println(liveOpEventTable.getDDL());
//        System.out.println(liveOpEventTable.preparedSelect(null, "WHERE :key_type"));
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Injector injector = Injection.getInjector(DusapModule.class);
    }

    public static class LiveOpKey {
        private String type;
        private long id;
    }

    public static class LiveOpConfig {
        private String type;
        private Object data;
    }

    public static class LiveOpEvent {
        private LiveOpKey key;
        private LiveOpConfig config;
        private Instant starts;
    }

    public static class DbInstant extends AbstractDbType<Instant> {
        public DbInstant() {
            super(JDBCType.BIGINT);
        }

        @Override
        protected Instant getResultImpl(final ResultSet rset, final int index) throws SQLException {
            return Instant.ofEpochMilli(rset.getLong(index));
        }

        @Override
        protected void setParameterImpl(final PreparedStatement stmt, final int index, final Instant value)
                throws SQLException {
            stmt.setLong(index, value.toEpochMilli());
        }
    }
}
