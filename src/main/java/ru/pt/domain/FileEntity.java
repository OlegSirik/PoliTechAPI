package ru.pt.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "pt_files")
public class FileEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "file_type", nullable = false, length = 30)
    private String fileType;

    @Column(name = "file_desc", nullable = false, length = 300)
    private String fileDesc;

    @Column(name = "product_code", nullable = false, length = 30)
    private String productCode;

    @Column(name = "package_code", nullable = false)
    private Integer packageCode;

    @Column(name = "file_body")
    private byte[] fileBody;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getFileDesc() { return fileDesc; }
    public void setFileDesc(String fileDesc) { this.fileDesc = fileDesc; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Integer getPackageCode() { return packageCode; }
    public void setPackageCode(Integer packageCode) { this.packageCode = packageCode; }
    public byte[] getFileBody() { return fileBody; }
    public void setFileBody(byte[] fileBody) { this.fileBody = fileBody; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}


