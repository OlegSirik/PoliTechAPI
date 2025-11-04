package ru.pt.domain.calculator;

public class FormulaLine {
    private Integer nr;
    private String conditionLeft;
    private String conditionOperator;
    private String conditionRight;
    private String expressionResult;
    private String expressionLeft;
    private String expressionOperator;
    private String expressionRight;
    private String postProcessor;

    public Integer getNr() { return nr; }
    public void setNr(Integer nr) { this.nr = nr; }
    public String getConditionLeft() { return conditionLeft; }
    public void setConditionLeft(String conditionLeft) { this.conditionLeft = conditionLeft; }
    public String getConditionOperator() { return conditionOperator; }
    public void setConditionOperator(String conditionOperator) { this.conditionOperator = conditionOperator; }
    public String getConditionRight() { return conditionRight; }
    public void setConditionRight(String conditionRight) { this.conditionRight = conditionRight; }
    public String getExpressionResult() { return expressionResult; }
    public void setExpressionResult(String expressionResult) { this.expressionResult = expressionResult; }
    public String getExpressionLeft() { return expressionLeft; }
    public void setExpressionLeft(String expressionLeft) { this.expressionLeft = expressionLeft; }
    public String getExpressionOperator() { return expressionOperator; }
    public void setExpressionOperator(String expressionOperator) { this.expressionOperator = expressionOperator; }
    public String getExpressionRight() { return expressionRight; }
    public void setExpressionRight(String expressionRight) { this.expressionRight = expressionRight; }
    public String getPostProcessor() { return postProcessor; }
    public void setPostProcessor(String postProcessor) { this.postProcessor = postProcessor; }
}


