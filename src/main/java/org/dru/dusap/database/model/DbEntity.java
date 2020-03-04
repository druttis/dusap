package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class DbEntity<T> {
    private static final Supplier<?> NULL_SUPPLIER = () -> null;

    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> getNullSupplier() {
        return (Supplier<T>) NULL_SUPPLIER;
    }

    private final DbContext context;
    private String name;
    private final Class<T> type;
    private Supplier<T> defaultSupplier;
    private final List<DbMember<?>> members;

    protected DbEntity(final DbContext context, final String name, final Class<T> type) {
        this.context = Objects.requireNonNull(context, "context");
        setName(name);
        this.type = type;
        setDefaultSupplier(null);
        members = new ArrayList<>();
    }

    public final DbContext getContext() {
        return context;
    }

    public final String getName() {
        return name;
    }

    public final String getDbName() {
        return String.format("`%s`", getName());
    }

    public final Class<T> getType() {
        return type;
    }

    public final Supplier<T> getDefaultSupplier() {
        return defaultSupplier;
    }

    public final boolean hasMembers() {
        return !members.isEmpty();
    }

    public final List<DbMember<?>> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public final DbMember<?> getMember(final String name) {
        final DbMember<?> member = getMemberOrNull(name);
        if (member == null) {
            throw new IllegalArgumentException("no such member: name=" + name);
        }
        return member;
    }

    @SuppressWarnings("unchecked")
    public final <C> DbMember<C> getMember(final String name, final Class<C> type) {
        Objects.requireNonNull(type, "type");
        final DbMember<?> member = getMember(name);
        if (!member.getType().equals(type)) {
            throw new IllegalArgumentException("type mismatch: type=" + type + ", memberType=" + member.getType());
        }
        return (DbMember<C>) member;
    }

    public final <C> DbMember<C> newMember(final String name, final Class<C> type) {
        final DbMember<C> member = new DbMember<>(getContext(), getTable(), this, name, type, null);
        addMember(member);
        return member;
    }

    public final int getColumnCount() {
        return getColumns().size();
    }

    public final void setParameter(final PreparedStatement stmt, final int index, final T value) throws SQLException {
        setParameterRaw(stmt, index, value);
    }

    protected final void getResultInto(final ResultSet rset, final int index, final T target) throws SQLException {
        Objects.requireNonNull(rset, "rset");
        Objects.requireNonNull(target, "instance");
        int local = index;
        for (final DbMember<?> member : getMembers()) {
            final Object value = member.getResult(rset, local);
            ReflectionUtils.setField(target, member.getField(), value);
            local += member.getColumnCount();
        }
    }

    protected final void setParameterFrom(final PreparedStatement stmt, final int index, final T source)
            throws SQLException {
        Objects.requireNonNull(stmt, "stmt");
        Objects.requireNonNull(source, "source");
        int local = index;
        for (final DbMember<?> member : getMembers()) {
            final Object value = ReflectionUtils.getField(source, member.getField());
            member.setParameterRaw(stmt, local, value);
            local += member.getColumnCount();
        }
    }

    protected final void setName(final String name) {
        Objects.requireNonNull(name, "name");
        this.name = name;
    }

    protected final void setDefaultSupplier(final Supplier<T> supplier) {
        defaultSupplier = (supplier != null ? supplier : getNullSupplier());
    }

    protected final void setDefaultValue(final T value) {
        setDefaultSupplier(value != null ? () -> value : null);
    }

    protected final DbMember<?> getMemberOrNull(final String name) {
        Objects.requireNonNull(name, "name");
        for (final DbMember<?> member : members) {
            if (member.getName().equals(name)) {
                return member;
            }
        }
        return null;
    }

    protected final void addMember(final DbMember<?> member) {
        Objects.requireNonNull(member, "member");
        final String name = member.getName();
        final DbMember<?> existing = getMemberOrNull(name);
        if (existing != null) {
            throw new IllegalArgumentException("member already exist: name=" + name);
        }
        members.add(member);
    }

    public abstract DbTable<?> getTable();

    public abstract List<DbMember<?>> getColumns();

    public abstract T getResult(final ResultSet rset, final int index) throws SQLException;

    public abstract String getDDL();

    protected abstract void setParameterRaw(PreparedStatement stmt, int index, Object value) throws SQLException;
}
