package ru.pt.process.service;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.product.LobModel;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.ProductVersionModel;
import ru.pt.api.dto.product.VarDataType;
import ru.pt.api.service.process.PreProcessService;
import ru.pt.process.utils.JsonProjection;
import ru.pt.process.utils.JsonSetter;
import ru.pt.process.utils.PeriodUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
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
                var.setVarValue(getMagicValue(lobVars, var.getVarCode(), policy));
            }
        }

        lobVars.add(new LobVar("product", "product", "product", "IN", productCode, VarDataType.STRING));
        lobVars.add(new LobVar("packageCode", "package", "package", "IN", packageCode.toString(), VarDataType.STRING));
        return lobVars;
    }

    public static String getMagicValue(List<LobVar> varDefs, String key, String policy) {
        JsonProjection projection = new JsonProjection(policy);

        LobVar varDef;
        try {
            switch (key) {
                case "ph_isMale":
                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_gender"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        return "M".equals(varDef.getVarValue()) ? "X" : "";
                    }
                    return "";
                case "ph_isFemale":
                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_gender"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        return "F".equals(varDef.getVarValue()) ? "X" : "";
                    }
                    return "";
                case "ph_age_issue":

                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_birthdate"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        LocalDate birthDate = LocalDate.parse(varDef.getVarValue());
                        LocalDate issueDate = projection.getIssueDate().toLocalDate();
                        return Integer.toString(Period.between(birthDate, issueDate).getYears());
                    } else {
                        return null;
                    }
                case "io_age_issue":
                    try {
                        varDef = varDefs.stream()
                                .filter(v -> v.getVarCode().equals("io_birthDate"))
                                .findFirst()
                                .orElse(null);
                        if (varDef != null) {
                            return Integer.toString(
                                    Period.between(
                                            LocalDate.parse(varDef.getVarValue()), projection.getIssueDate().toLocalDate()
                                    ).getYears()
                            );
                        }
                    } catch (Exception e) {
                        return "-1";
                    }
                    return "";
                case "io_age_end":
                    try {
                        varDef = varDefs.stream()
                                .filter(v -> v.getVarCode().equals("io_birthDate"))
                                .findFirst()
                                .orElse(null);
                        if (varDef != null) {
                            return Integer.toString(
                                    Period.between(
                                                    LocalDate.parse(varDef.getVarValue()), projection.getEndDate().toLocalDate())
                                            .getYears()
                            );
                        }
                        return "-1";
                    } catch (Exception e) {
                        return "-1";
                    }
                case "policyTermMonths":
                    LocalDate st = projection.getStartDate().toLocalDate();
                    LocalDate ed = projection.getEndDate().toLocalDate();
                    Period p = Period.between(st, ed);
                    int m = p.getYears() * 12 + p.getMonths();
                    return Integer.toString(m);

                default:
                    return key + " Not Found";
            }
        } catch (Exception e) {
            return "";
        }
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
