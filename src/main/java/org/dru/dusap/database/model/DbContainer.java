package org.dru.dusap.database.model;

import java.util.List;

public interface DbContainer {
    boolean hasMembers();

    List<DbMember<?>> getMembers();

    <F> DbMember<F> getMember(String name);

    <F> DbMember<F> newMember(String name, Class<F> type);

    List<DbMember<?>> getColumns();
}
