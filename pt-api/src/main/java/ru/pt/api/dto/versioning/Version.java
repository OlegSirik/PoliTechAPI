package ru.pt.api.dto.versioning;

/**
 * DTO для описания версии договора
 */
public class Version {

    // номер версии доступной для редактирования
    private Integer devVersion;
    // номер версии, которая недоступна для редактирования
    private Integer prodVersion;

    public Version(Integer devVersion, Integer prodVersion) {
        this.devVersion = devVersion;
        this.prodVersion = prodVersion;
    }

    public Version() {
    }

    public Integer getDevVersion() {
        return devVersion;
    }

    public void setDevVersion(Integer devVersion) {
        this.devVersion = devVersion;
    }

    public Integer getProdVersion() {
        return prodVersion;
    }

    public void setProdVersion(Integer prodVersion) {
        this.prodVersion = prodVersion;
    }

}
