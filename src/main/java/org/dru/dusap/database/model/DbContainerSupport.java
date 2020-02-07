package org.dru.dusap.database.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DbContainerSupport implements DbContainer {
    private final DbContainer owner;
    private final List<DbMember> members;

    public DbContainerSupport(final DbContainer owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
        members = new ArrayList<>();
    }

    @Override
    public List<DbMember> getMembers() {
        return members;
    }

    @Override
    public List<DbColumn> getColumns() {
        return getMembers().stream().flatMap(member -> {
            if (member instanceof DbComplex) {
                return ((DbComplex) member).getMembers().stream();
            } else {
                return Stream.of(member);
            }
        }).map(DbColumn.class::cast).collect(Collectors.toList());
    }

    @Override
    public void addMember(final DbMember member) {
        members.add(member);
        member.setParent(owner);
    }

    @Override
    public void replaceColumnWithComplex(final DbColumn column, final DbComplex complex) {
        final int index = members.indexOf(column);
        members.set(index, complex);
        column.setParent(null);
        complex.setParent(owner);
    }

    @Override
    public String getQName(final String childName) {
        return owner.getQName(childName);
    }
}
