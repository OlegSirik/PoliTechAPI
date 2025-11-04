package ru.pt.domain;

import jakarta.persistence.*;
import ru.pt.domain.calculator.CalculatorModel;

@Entity
@Table(name = "pt_calculators")
@SequenceGenerator(name = "pt_calculators_seq_gen", sequenceName = "pt_calculators_seq", allocationSize = 1)
public class CalculatorEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "product_code", nullable = false, length = 30)
    private String productCode;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "package_no", nullable = false)
    private Integer packageNo;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "calculator", columnDefinition = "jsonb", nullable = false)
    private CalculatorModel calculator;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public Integer getPackageNo() { return packageNo; }
    public void setPackageNo(Integer packageNo) { this.packageNo = packageNo; }
    public CalculatorModel getCalculator() { return calculator; }
    public void setCalculator(CalculatorModel calculator) { this.calculator = calculator; }
}


