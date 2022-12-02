package org.dru.dusap.util;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://semver.org/">Semantic versioning 2.0</a>
 */
public final class Version implements Comparable<Version>, Serializable {
    private static final String REGEX = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*" +
            "[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+" +
            "(?:\\.[0-9a-zA-Z-]+)*))?$";
    private final static Pattern PATTERN = Pattern.compile(REGEX);

    public static Version of(final String str) {
        final Matcher matcher = PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("str");
        }
        final int major = Integer.parseInt(matcher.group(1));
        final int minor = Integer.parseInt(matcher.group(2));
        final int patch = Integer.parseInt(matcher.group(3));
        final String prerelease = matcher.group(4);
        final String build = matcher.group(5);
        return new Version(major, minor, patch, prerelease, build);
    }

    private int major;
    private int minor;
    private int patch;
    private String prerelease;
    private String build;

    private Version(final int major, final int minor, final int patch, final String prerelease, final String build) {
        if (major < 0) {
            throw new IllegalArgumentException("negative major: " + major);
        }
        if (minor < 0) {
            throw new IllegalArgumentException("negative minor: " + minor);
        }
        if (patch < 0) {
            throw new IllegalArgumentException("negative patch: " + patch);
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prerelease = prerelease;
        this.build = build;
    }

    private Version() {
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

    public String getPrerelease() {
        return prerelease;
    }

    public String getBuild() {
        return build;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Version version = (Version) o;
        return getMajor() == version.getMajor()
                && getMinor() == version.getMinor()
                && getPatch() == version.getPatch()
                && getPrerelease().equals(version.getPrerelease())
                && getBuild().equals(version.getBuild());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMajor(), getMinor(), getPatch(), getPrerelease(), getBuild());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getMajor()).append('.').append(getMinor()).append('.').append(getPatch());
        final String prerelease = getPrerelease();
        if (prerelease != null) {
            sb.append('-');
            sb.append(prerelease);
        }
        final String build = getBuild();
        if (build != null) {
            sb.append(build);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(final Version other) {
        if (other == null) {
            return 0;
        }
        final int major = Integer.compareUnsigned(getMajor(), other.getMajor());
        if (major != 0) {
            return major;
        }
        final int minor = Integer.compareUnsigned(getMinor(), other.getMinor());
        if (minor != 0) {
            return minor;
        }
        return Integer.compareUnsigned(getPatch(), other.getPatch());
    }
}
