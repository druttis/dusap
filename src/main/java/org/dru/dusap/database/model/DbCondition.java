package org.dru.dusap.database.model;

public final class DbCondition<T> {
    private final DbMember<T> field;
    private final String image;
    private final int parameterCount;

    public DbCondition(final DbMember<T> field, final String image) {
        this.field = field;
        this.image = image;
        parameterCount = computeParameterCount();
    }

    public DbMember<T> getField() {
        return field;
    }

    public String getImage() {
        return image;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public String getSQL() {
        return String.format("%s%s", getField().getDbName(), getImage());
    }

    private int computeParameterCount() {
        int result = 0;
        for (int index = 0; index < getImage().length(); index++) {
            if (getImage().charAt(index) == '?') {
                result++;
            }
        }
        return result;
    }
}
