package org.dru.dusap.database.model;

import java.util.List;

public final class DbDelete extends DbConditional {
    public static Builder where(final DbMember<?> field, final String image) {
        return new Builder().where(field, image);
    }

    private DbDelete(final List<DbMember<?>> fields, final DbTable<?> table, final List<DbCondition<?>> conditions) {
        super(fields, table, conditions);
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(getTable().getDbName());
        appendWhereSQL(sb);
        System.out.println(sb.toString());
        return sb.toString();
    }

    @Override
    protected int getFirstConditionIndex() {
        return 1;
    }

    public static final class Builder extends DbConditional.Builder {
        private Builder() {
        }

        public Builder where(final DbMember<?> members, final String image) {
            addCondition(members, image);
            return this;
        }

        public DbDelete build() {
            return new DbDelete(getFields(), getTable(), getConditions());
        }
    }
}
