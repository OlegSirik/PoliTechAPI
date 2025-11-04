package ru.pt.domain.lob;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LobVar {
    
    @JsonProperty("varDataType")
    private VarDataType varDataType;

    @JsonProperty("varCode")
    private String varCode;

    @JsonProperty("varName")
    private String varName;
    
    @JsonProperty("varPath")
    private String varPath;
    
    @JsonProperty("varType")
    private String varType;
    
    @JsonProperty("varValue")
    private String varValue = "";
    
    // Constructors
    public LobVar() {}
    
    public LobVar(String varCode, String varName, String varPath, String varType, VarDataType varDataType) {
        this.varCode = varCode;
        this.varName = varName;
        this.varPath = varPath;
        this.varType = varType;
        this.varDataType = varDataType;
    }
    public LobVar(String varCode, String varName, String varPath, String varType, String varValue, VarDataType varDataType) {
        this.varCode = varCode;
        this.varName = varName;
        this.varPath = varPath;
        this.varType = varType;
        this.varValue = varValue;
        this.varDataType = varDataType;
    }
    
    // Getters and Setters
    public String getVarCode() {
        return varCode;
    }
    
    public void setVarCode(String varCode) {
        this.varCode = varCode;
    }
    
    public String getVarName() {
        return varName;
    }
    
    public void setVarName(String varName) {
        this.varName = varName;
    }
    
    public String getVarPath() {
        return varPath;
    }
    
    public void setVarPath(String varPath) {
        this.varPath = varPath;
    }
    
    public String getVarType() {
        return varType;
    }
    
    public void setVarType(String varType) {
        this.varType = varType;
    }
    
    public String getVarValue() {
        if (varValue == null) varValue = "";
        return varValue;
    }
    
    public void setVarValue(String varValue) {
        this.varValue = varValue;
    }

    public VarDataType getVarDataType() {
        return varDataType;
    }

    public void setVarDataType(VarDataType varDataType) {
        this.varDataType = varDataType;
    }
}
