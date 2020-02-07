package org.dru.dusap.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {
    private static final Pattern PATTERN = Pattern.compile("([0]|[1-9][0-9]*).([0]|[1-9][0-9]*).([0]|[1-9][0-9]*)");

    public static Version decode(final String str) {
        final Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            return new Version(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        } else {
            throw new IllegalArgumentException(PATTERN.toString());
        }
    }

    private int major;
    private int minor;
    private int patch;

    private Version() {
    }

    public Version(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        final Version version = (Version) o;
        return getMajor() == version.getMajor() && getMinor() == version.getMinor() && getPatch() == version.getPatch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMajor(), getMinor(), getPatch());
    }

    @Override
    public int compareTo(final Version o) {
        if (major < o.major) {
            return -1;
        } else if (major > o.major) {
            return 1;
        } else if (minor < o.minor) {
            return -1;
        } else if (minor > o.minor) {
            return 1;
        } else if (patch < o.patch) {
            return -1;
        } else if (patch > o.patch) {
            return 1;
        } else {
            return 0;
        }
    }
}
