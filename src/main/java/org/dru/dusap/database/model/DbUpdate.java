package org.dru.dusap.database.model;

import java.util.Objects;

public final class DbUpdate extends DbStatement {
    private static abstract class Assignment<C> {
        protected DbColumn<C> column;

        protected Assignment(final DbColumn<C> column) {
            Objects.requireNonNull(column, "column");
            this.column = column;
        }
    }
}
