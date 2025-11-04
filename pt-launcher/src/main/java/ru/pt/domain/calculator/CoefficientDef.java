package ru.pt.domain.calculator;

import java.util.ArrayList;
import java.util.List;

public class CoefficientDef {
    private String varCode;
    private String varName;
    private List<CoefficientColumn> columns = new ArrayList<>();

    public String getVarCode() { return varCode; }
    public void setVarCode(String varCode) { this.varCode = varCode; }
    public String getVarName() { return varName; }
    public void setVarName(String varName) { this.varName = varName; }
    public List<CoefficientColumn> getColumns() { return columns; }
    public void setColumns(List<CoefficientColumn> columns) { this.columns = columns; }
}


