package ru.pt.process.service;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.process.Cover;
import ru.pt.api.dto.process.CoverInfo;
import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.product.*;
import ru.pt.api.service.process.PreProcessService;
import ru.pt.process.utils.JsonProjection;
import ru.pt.process.utils.JsonSetter;
import ru.pt.process.utils.PeriodUtils;
import ru.pt.process.utils.VariablesService;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PreProcessServiceImpl implements PreProcessService {

    @Override
    public String enrichPolicy(String policy, ProductVersionModel productVersionModel) {
        JsonProjection projection = new JsonProjection(policy);
        String newJson;
        // Policy
        if (projection.getIssueDate() == null) {
            JsonSetter setter = new JsonSetter(policy);
            setter.setRawValue("issueDate", ZonedDateTime.now().toString());
            newJson = setActivationDelay(setter.writeValue(), productVersionModel);
        } else {
            newJson = setActivationDelay(policy, productVersionModel);
        }

        try {
            return setPolicyTerm(newJson, productVersionModel);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<LobVar> evaluateAndEnrichVariables(String policy, LobModel lobModel, String productCode) {
        JsonProjection projection = new JsonProjection(policy);

        Integer packageCode = projection.getPackageCode();

        List<LobVar> lobVars = lobModel.getMpVars();


        for (LobVar var : lobVars) {
            if ("IN".equals(var.getVarType())) {
                try {
                    String value = projection.evaluateJsonPath(var.getVarPath());
                    var.setVarValue(value == null ? "" : value);
                } catch (Exception e) {
                    var.setVarValue("");
                }
            }
        }

        for (LobVar var : lobVars) {
            if ("MAGIC".equals(var.getVarType())) {
                var.setVarValue(VariablesService.getMagicValue(lobVars, var.getVarCode(), policy));
            }
        }

        lobVars.add(new LobVar("product", "product", "product", "IN", productCode, VarDataType.STRING));
        lobVars.add(new LobVar("packageCode", "package", "package", "IN", packageCode.toString(), VarDataType.STRING));
        return lobVars;
    }

    @Override
    public void enrichVariablesBeforeCalculation(InsuredObject insObject, List<LobVar> lobVars) {
        if (insObject != null && insObject.getCovers() != null) {
            for (Cover cover : insObject.getCovers()) {
                if (cover.getCover() != null) {
                    Double sumInsured = cover.getSumInsured();
                    Double premium = cover.getPremium();

                    String sumInsuredVarCode = cover.getCover().getCode() + "_SumIns";
                    String premiumVarCode = cover.getCover().getCode() + "_Prem";

                    LobVar lobVar = new LobVar();
                    lobVar.setVarCode(sumInsuredVarCode);
                    lobVar.setVarValue(sumInsured != null ? sumInsured.toString() : null);
                    lobVar.setVarType("VAR");
                    lobVars.add(lobVar);

                    lobVar = new LobVar();
                    lobVar.setVarCode(premiumVarCode);
                    lobVar.setVarValue(premium != null ? premium.toString() : null);
                    lobVar.setVarType("VAR");
                    lobVars.add(lobVar);
                }
            }
        }
    }

    @Override
    public InsuredObject getInsuredObject(String policy, ProductVersionModel productVersionModel) {
        JsonProjection projection = new JsonProjection(policy);

        var insuredObject = projection.getInsuredObject();

        if (insuredObject == null || insuredObject.getCovers() == null) {
            var emptyInsuredObject = new InsuredObject();
            emptyInsuredObject.setCovers(new ArrayList<>());
            insuredObject = emptyInsuredObject;
        }
        Integer inPackageNo;
        if (insuredObject.getPackageCode() == null) {
            inPackageNo = 0;
        } else {
            inPackageNo = insuredObject.getPackageCode();
        }
        final Integer pkgCode = inPackageNo;

        PvPackage pvPackage = productVersionModel.getPackages().stream()
                .filter(p -> p.getCode().equals(pkgCode))
                .findFirst()
                .orElse(null);

        if (pvPackage == null) {
            throw new IllegalArgumentException("Package not found: " + inPackageNo);
        }

        insuredObject.setPackageCode(pkgCode);

        List<PvCover> covers = pvPackage.getCovers();
        for (PvCover pvCover : covers) {
            // Check if the cover.code exists in policy.covers
            List<Cover> policyCovers = insuredObject.getCovers();
            boolean coverExists = false;
            if (policyCovers != null) {
                for (Cover policyCover : policyCovers) {
                    if (policyCover != null && policyCover.getCover() != null && pvCover.getCode().equals(policyCover.getCover().getCode())) {
                        coverExists = true;
                        break;
                    }
                }
            }
            if (!coverExists && pvCover.getIsMandatory()) {
                Cover newCover = new Cover();
                newCover.setCover(new CoverInfo(pvCover.getCode(), "", ""));
                coverExists = true;
                insuredObject.getCovers().add(newCover);
            }
            if (coverExists) {
                Cover policyCover = policyCovers.stream()
                        .filter(c -> c.getCover() != null && c.getCover().getCode().equals(pvCover.getCode()))
                        .findFirst()
                        .orElse(null);
                if (policyCover != null) {
                    String waitingPeriod = pvCover.getWaitingPeriod();
                    if (waitingPeriod != null && !waitingPeriod.isEmpty()) {
                        ZonedDateTime startDate = projection.getStartDate().plus(Period.parse(waitingPeriod));
                        policyCover.setStartDate(startDate);
                    } else {
                        policyCover.setStartDate(projection.getStartDate());
                    }
                    String coverageTerm = pvCover.getCoverageTerm();
                    if (coverageTerm != null && !coverageTerm.isEmpty()) {
                        ZonedDateTime endDate = policyCover.getStartDate().plus(Period.parse(coverageTerm));
                        policyCover.setEndDate(endDate);
                    } else {
                        policyCover.setEndDate(projection.getEndDate());
                    }


                    PvLimit pvLimit = VariablesService.getPvLimit(pvCover, policyCover.getSumInsured());
                    if (pvLimit != null) {
                        policyCover.setSumInsured(pvLimit.getSumInsured());
                        policyCover.setPremium(pvLimit.getPremium());
                    }

                    policyCover.setDeductibleCur(null);
                    policyCover.setDeductibleMin(null);
                    policyCover.setDeductiblePercent(null);


                    PvDeductible pvDeductible = VariablesService.getPvDeductible(pvCover, policyCover);
                    if (pvDeductible != null) {
                        policyCover.setDeductible(pvDeductible.getDeductible());
                        policyCover.setDeductibleType(pvDeductible.getDeductibleType());
                        policyCover.setDeductibleSpecific(pvDeductible.getDeductibleSpecific());
                        policyCover.setDeductibleUnit(pvDeductible.getDeductibleUnit());
                    } else {
                        policyCover.setDeductible(null);
                        policyCover.setDeductibleType(null);
                        policyCover.setDeductibleSpecific(null);
                        policyCover.setDeductibleUnit(null);
                    }

                    policyCover.setCover(new CoverInfo(pvCover.getCode(), "", ""));
                }
            }
        }

        return insuredObject;
    }


    public String setActivationDelay(String policy, ProductVersionModel policyVersionModel) {

        JsonProjection projection = new JsonProjection(policy);

        String validatorType = policyVersionModel.getWaitingPeriod().getValidatorType();

        String validatorValue = policyVersionModel.getWaitingPeriod().getValidatorValue();

        ZonedDateTime issueDate = projection.getIssueDate();

        ZonedDateTime startDate = projection.getStartDate();

        String waitingPeriod = projection.getWaitingPeriod();

        JsonSetter setter = new JsonSetter(policy);

        if (issueDate == null) {
            throw new IllegalAccessError("Issue date is required");
        }
        switch (validatorType) {
            case "RANGE":
                if (startDate == null) {
                    throw new IllegalAccessError("Start date is required");
                }

                if (!PeriodUtils.isDateInRange(issueDate, startDate, validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                setter.setRawValue("waitingPeriod", validatorValue);
                break;
            case "LIST":
                // список доступных значений из модели полиса
                // взять из договора policyTerm, проверить что это значение есть в списке. вычислить дату2
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                } else if (list.length == 1) {
                    waitingPeriod = list[0];
                } else {
                    if (waitingPeriod == null) {
                        throw new IllegalAccessError("Waiting period is required");
                    }

                    boolean found = false;
                    // check if policyTerm is in list array. loop through list and check if policyTerm is in list
                    for (String period : list) {
                        if (waitingPeriod.equals(period.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalAccessError("Waiting period is not in list");
                    }
                }

                startDate = issueDate.plus(Period.parse(waitingPeriod));

                setter.setRawValue("startDate", startDate.toString());
                setter.setRawValue("waitingPeriod", waitingPeriod);
                break;
            case "NEXT_MONTH":
                startDate = issueDate.plus(Period.parse("P1M")).withDayOfMonth(1);
                setter.setRawValue("startDate", startDate.toString());
                break;
        }

        return setter.writeValue();
    }

    public String setPolicyTerm(String policy, ProductVersionModel policyVersionModel) {
        // activationDelay - RANGE LIST NEXT_MONTH
        String validatorType = policyVersionModel.getPolicyTerm().getValidatorType();
        String validatorValue = policyVersionModel.getPolicyTerm().getValidatorValue();

        var projection = new JsonProjection(policy);

        ZonedDateTime startDate = projection.getStartDate();
        ZonedDateTime endDate = projection.getEndDate();

        String policyTerm = projection.getPolicyTerm();

        JsonSetter setter = new JsonSetter(policy);

        if (startDate == null) {
            throw new BadRequestException("start date is required");
        }

        switch (validatorType) {
            case "RANGE":
                if (endDate == null) {
                    throw new BadRequestException("End date is required");
                }

                if (!PeriodUtils.isDateInRange(startDate, endDate, validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                setter.setRawValue("policyTerm", validatorValue);
                break;
            case "LIST":
                // должно быть startDate и policyTerm в договоре и policyTerms в модели полиса
                // список доступных значений из модели полиса
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                } else if (list.length == 1) {
                    policyTerm = list[0];
                } else {
                    if (policyTerm == null) {
                        throw new IllegalAccessError("Policy term is required");
                    }

                    boolean found = false;
                    // check if policyTerm is in list array. loop through list and check if policyTerm is in list
                    for (String period : list) {
                        if (policyTerm.equals(period.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Policy term is not in list");
                    }
                }

                endDate = startDate.plus(Period.parse(policyTerm));

                setter.setRawValue("endDate", endDate.toString());
                setter.setRawValue("policyTerm", policyTerm);
                break;
        }

        return policy;
    }


}
