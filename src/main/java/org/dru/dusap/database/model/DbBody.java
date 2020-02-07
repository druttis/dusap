package org.dru.dusap.database.model;

import java.util.List;

final class DbBody implements DbContainer {
    private final DbContainerSupport containerSupport;

    DbBody() {
        containerSupport = new DbContainerSupport(this);
    }

    @Override
    public List<DbMember> getMembers() {
        return containerSupport.getMembers();
    }

    @Override
    public List<DbColumn> getColumns() {
        return containerSupport.getColumns();
    }

    @Override
    public void addMember(final DbMember member) {
        containerSupport.addMember(member);
    }

    @Override
    public void replaceColumnWithComplex(final DbColumn column, final DbComplex complex) {
        containerSupport.replaceColumnWithComplex(column, complex);
    }

    @Override
    public String getQName(final String childName) {
        return childName;
    }

    public DbColumn getColumn(final String qname) {
        return getColumns().stream()
                .filter(column -> column.getQName().equals(qname))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no such column: qname=" + qname));
    }
}
