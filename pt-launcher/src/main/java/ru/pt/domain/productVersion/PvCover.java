package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PvCover {

	@JsonProperty("code")
	private String code;

	@JsonProperty("deductibles")
	private List<PvDeductible> deductibles;

	@JsonProperty("waitingPeriod")
	private String waitingPeriod;

	@JsonProperty("isMandatory")
	private Boolean isMandatory;

	@JsonProperty("coverageTerm")
	private String coverageTerm;

	@JsonProperty("isDeductibleMandatory")
	private Boolean isDeductibleMandatory;

	@JsonProperty("limits")
	private List<PvLimit> limits;

	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	public List<PvDeductible> getDeductibles() { return deductibles; }
	public void setDeductibles(List<PvDeductible> deductibles) { this.deductibles = deductibles; }

	public String getWaitingPeriod() { return waitingPeriod; }
	public void setWaitingPeriod(String waitingPeriod) { this.waitingPeriod = waitingPeriod; }

	public Boolean getIsMandatory() { return isMandatory; }
	public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

	public String getCoverageTerm() { return coverageTerm; }
	public void setCoverageTerm(String coverageTerm) { this.coverageTerm = coverageTerm; }

	public Boolean getIsDeductibleMandatory() { return isDeductibleMandatory; }
	public void setIsDeductibleMandatory(Boolean isDeductibleMandatory) { this.isDeductibleMandatory = isDeductibleMandatory; }

	public List<PvLimit> getLimits() { return limits; }
	public void setLimits(List<PvLimit> limits) { this.limits = limits; }
}


