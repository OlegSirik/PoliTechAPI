package ru.pt.api.dto.file;

public class FileModel {

    private Long id;
    private String fileType;
    private String fileDescription;
    private String productCode;
    private Integer packageCode;
    private byte[] fileBody;
    private Boolean deleted;

    public FileModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(Integer packageCode) {
        this.packageCode = packageCode;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
