package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodRule {

	@JsonProperty("validatorType")
	private String validatorType;

	@JsonProperty("validatorValue")
	private String validatorValue;



	public String getValidatorType() { return validatorType; }
	public void setValidatorType(String validatorType) { this.validatorType = validatorType; }

	public String getValidatorValue() { return validatorValue; }
	public void setValidatorValue(String validatorValue) { this.validatorValue = validatorValue; }
}


