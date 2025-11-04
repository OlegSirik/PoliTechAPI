package ru.pt.domain.calculator;

public class CoefficientColumn {
    private String varCode;
    private String varDataType;
    private Integer nr;
    private String conditionOperator;
    private String sortOrder;

    public String getVarCode() { return varCode; }
    public void setVarCode(String varCode) { this.varCode = varCode; }
    public String getVarDataType() { return varDataType; }
    public void setVarDataType(String varDataType) { this.varDataType = varDataType; }
    public Integer getNr() { return nr; }
    public void setNr(Integer nr) { this.nr = nr; }
    public String getConditionOperator() { return conditionOperator; }
    public void setConditionOperator(String conditionOperator) { this.conditionOperator = conditionOperator; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}


