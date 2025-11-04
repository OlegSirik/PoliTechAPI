package ru.pt.domain.calculator;

import java.util.ArrayList;
import java.util.List;

public class FormulaDef {
    private String varCode;
    private String varName;
    private List<FormulaLine> lines = new ArrayList<>();

    public String getVarCode() { return varCode; }
    public void setVarCode(String varCode) { this.varCode = varCode; }
    public String getVarName() { return varName; }
    public void setVarName(String varName) { this.varName = varName; }
    public List<FormulaLine> getLines() { return lines; }
    public void setLines(List<FormulaLine> lines) { this.lines = lines; }
}


