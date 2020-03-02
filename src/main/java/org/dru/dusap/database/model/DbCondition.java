package org.dru.dusap.database.model;

public final class DbCondition<T> {
    private final DbColumn<T> column;
    private final String image;
    private final int parameterCount;

    public DbCondition(final DbColumn<T> column, final String image) {
        this.column = column;
        this.image = image;
        parameterCount = computeParameterCount();
    }

    public DbColumn<T> getColumn() {
        return column;
    }

    public String getImage() {
        return image;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public String getSQL() {
        return String.format("%s%s", getColumn().getDbName(), getImage());
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
