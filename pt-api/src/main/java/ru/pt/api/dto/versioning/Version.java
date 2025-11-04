package ru.pt.api.dto.versioning;

/**
 * DTO для описания версии договора
 */
public class Version {
    // 1.2.12-dev
    private Integer major;
    private Integer minor;
    private Integer patch;
    private String prefix;

    public Version() {
    }

    public Version(Integer major, Integer minor, Integer patch, String prefix) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prefix = prefix;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getPatch() {
        return patch;
    }

    public void setPatch(Integer patch) {
        this.patch = patch;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return major + '.' + minor + '.' + patch + '-' + prefix;
    }

}
