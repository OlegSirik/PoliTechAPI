package ru.pt.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "pt_products")
public class Product {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "lob", nullable = false, length = 30)
    private String lob;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "prod_version_no")
    private Integer prodVersionNo;

    @Column(name = "dev_version_no")
    private Integer devVersionNo;

    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted = false;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLob() { return lob; }
    public void setLob(String lob) { this.lob = lob; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getProdVersionNo() { return prodVersionNo; }
    public void setProdVersionNo(Integer prodVersionNo) { this.prodVersionNo = prodVersionNo; }
    public Integer getDevVersionNo() { return devVersionNo; }
    public void setDevVersionNo(Integer devVersionNo) { this.devVersionNo = devVersionNo; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    
}


