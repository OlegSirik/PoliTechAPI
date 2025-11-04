package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidatorRule {

	@JsonProperty("lineNr")
	private Integer lineNr;

	@JsonProperty("keyRight")
	private String keyRight;

	@JsonProperty("keyLeft")
	private String keyLeft;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("ruleType")
	private String ruleType;

	@JsonProperty("errorText")
	private String errorText;

	@JsonProperty("valueRight")
	private String valueRight;

	public Integer getLineNr() { return lineNr; }
	public void setLineNr(Integer lineNr) { this.lineNr = lineNr; }

	public String getKeyRight() { return keyRight; }
	public void setKeyRight(String keyRight) { this.keyRight = keyRight; }

	public String getKeyLeft() { return keyLeft; }
	public void setKeyLeft(String keyLeft) { this.keyLeft = keyLeft; }

	public String getDataType() { return dataType; }
	public void setDataType(String dataType) { this.dataType = dataType; }

	public String getRuleType() { return ruleType; }
	public void setRuleType(String ruleType) { this.ruleType = ruleType; }

	public String getErrorText() { return errorText; }
	public void setErrorText(String errorText) { this.errorText = errorText; }

	public String getValueRight() { return valueRight; }
	public void setValueRight(String valueRight) { this.valueRight = valueRight; }
}


