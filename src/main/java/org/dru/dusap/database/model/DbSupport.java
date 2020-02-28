package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DbSupport implements DbContainer {
    private final DbEntity<?> owner;
    private final List<DbMember<?>> members;

    public DbSupport(final DbEntity<?> owner) {
        Objects.requireNonNull(owner, "owner");
        this.owner = owner;
        members = new ArrayList<>();
    }

    @Override
    public boolean hasMembers() {
        return !members.isEmpty();
    }

    @Override
    public List<DbMember<?>> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public <F> DbMember<F> getMember(final String name) {
        final DbMember<F> member = getMemberOrNull(name);
        if (member == null) {
            throw new IllegalArgumentException("no such member: name=" + name);
        }
        return member;
    }

    @Override
    public <F> DbMember<F> newMember(final String name, final Class<F> type) {
        final DbMember<F> member = new DbMember<>(owner, name, type, null);
        addMember(member);
        return member;
    }

    @Override
    public List<DbMember<?>> getColumns() {
        return getMembers().stream().flatMap(member -> {
            if (member.hasMembers()) {
                return member.getMembers().stream();
            } else {
                return Stream.of(member);
            }
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <F> DbMember<F> getMemberOrNull(final String name) {
        for (final DbMember<?> member : members) {
            if (member.getName().equals(name)) {
                return (DbMember<F>) member;
            }
        }
        return null;
    }

    public void addMember(final DbMember<?> member) {
        Objects.requireNonNull(member, "member");
        final String name = member.getName();
        if (getMemberOrNull(name) != null) {
            throw new IllegalArgumentException("duplicate members: name=" + name);
        }
        members.add(member);
    }

    public void populateMembers(final Class<?> type) {
        ReflectionUtils.getSerializableFields(type).forEach(field ->
                addMember(new DbMember<>(owner, owner.getQualifiedName(field.getName()), field.getType(), field)));
    }
}
