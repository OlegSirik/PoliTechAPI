package ru.pt.domain;


import jakarta.persistence.*;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.domain.productVersion.PvPackage;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pt_product_versions")
public class ProductVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Integer pk;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product", columnDefinition = "jsonb", nullable = false)
    private ProductVersionModel product;

    public Integer getPk() { return pk; }
    public void setPk(Integer pk) { this.pk = pk; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public ProductVersionModel getProduct() { return product; }
    public void setProduct(ProductVersionModel product) { this.product = product; }
    public List<PvPackage> getPackages() { return product.getPackages(); }
}


