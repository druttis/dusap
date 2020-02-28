package org.dru.dusap.database.type;

import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.json.JsonSerializerSupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DbTypesImpl implements DbTypes {
    private final Map<Class<?>, List<DbType<?>>> dbTypesByJavaType = new HashMap<>();
    private final Map<DbType<?>, List<Class<?>>> javaTypesByDbType = new HashMap<>();

    private final JsonSerializer jsonSerializer;

    public DbTypesImpl(final JsonSerializerSupplier jsonSerializerSupplier) {
        jsonSerializer = jsonSerializerSupplier.get();
        registerDefaults();
    }

    private void registerDefaults() {
        register(Boolean.class, Boolean.TYPE, DbBoolean.INSTANCE);
        register(Byte.class, Byte.TYPE, DbByte.INSTANCE);
        register(Short.class, Short.TYPE, DbShort.INSTANCE);
        register(Integer.class, Integer.TYPE, DbInteger.INSTANCE);
        register(Long.class, Long.TYPE, DbLong.INSTANCE);
        register(Float.class, Float.TYPE, DbFloat.INSTANCE);
        register(Double.class, Double.TYPE, DbDouble.INSTANCE);
        register(Character.class, Character.TYPE, DbCharacter.INSTANCE);
        registerDbType(String.class, DbString.INSTANCE);
        registerDbType(byte[].class, DbByteArray.INSTANCE);
    }

    public <T> void registerDbType(final Class<T> type, final DbType<T> dbType) {
        dbTypesByJavaType.compute(type, ($, dbTypes) -> {
            if (dbTypes == null) {
                dbTypes = new CopyOnWriteArrayList<>();
            }
            if (dbTypes.contains(dbType)) {
                throw new IllegalStateException(type.getName() + " -> " + dbType.getClass().getName()
                        + " mapping already exist");
            }
            javaTypesByDbType.compute(dbType, ($2, types) -> {
                if (types == null) {
                    types = new CopyOnWriteArrayList<>();
                }
                if (types.contains(type)) {
                    throw new IllegalStateException(dbType.getClass().getName() + " -> " + type.getName()
                            + " mapping already exist");
                }
                types.add(type);
                return types;
            });
            dbTypes.add(dbType);
            return dbTypes;
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DbType<T> getDbType(final Class<T> type) {
        final List<DbType<?>> dbTypes = dbTypesByJavaType.get(type);
        if (dbTypes == null || dbTypes.isEmpty()) {
            return DbJson.instance(type, jsonSerializer);
        } else {
            return (DbType<T>) dbTypes.get(0);
        }
    }

    private <T> void register(final Class<T> boxed, final Class<T> primitive, final DbType<T> dbType) {
        registerDbType(boxed, dbType);
        registerDbType(primitive, dbType);
    }
}
