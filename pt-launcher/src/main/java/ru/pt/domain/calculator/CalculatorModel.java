package ru.pt.domain.calculator;

import java.util.ArrayList;
import java.util.List;

import ru.pt.domain.lob.LobVar;

/**
 * Domain model representing calculator JSON structure from scripts/calculator.txt
 */
public class CalculatorModel {
    private Integer id;
    private Integer productId;
    private String productCode;
    private Integer versionNo;
    private Integer packageNo;
    private List<LobVar> vars = new ArrayList<>();
    private List<FormulaDef> formulas = new ArrayList<>();
    private List<CoefficientDef> coefficients = new ArrayList<>();

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
    public List<LobVar> getVars() { return vars; }
    public void setVars(List<LobVar> vars) { this.vars = vars; }
    public List<FormulaDef> getFormulas() { return formulas; }
    public void setFormulas(List<FormulaDef> formulas) { this.formulas = formulas; }
    public List<CoefficientDef> getCoefficients() { return coefficients; }
    public void setCoefficients(List<CoefficientDef> coefficients) { this.coefficients = coefficients; }
}


