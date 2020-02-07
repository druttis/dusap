package org.dru.dusap.database.model;

import java.util.List;

public interface DbContainer {
    List<DbMember> getMembers();

    List<DbColumn> getColumns();

    void addMember(final DbMember member);

    void replaceColumnWithComplex(final DbColumn column, final DbComplex complex);

    String getQName(String childName);
}
