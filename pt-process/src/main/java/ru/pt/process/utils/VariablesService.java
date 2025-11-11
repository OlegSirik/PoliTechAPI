package ru.pt.process.utils;

import ru.pt.api.dto.process.Cover;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.PvCover;
import ru.pt.api.dto.product.PvDeductible;
import ru.pt.api.dto.product.PvLimit;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

public class VariablesService {

    public static PvLimit getPvLimit(PvCover pvCover, Double sumInsured) {

        // если на покрытии только 1 лимит то он является единственно возможным
        // иначе проверяем, что переданная страховая сумма есть в списке возможных сумм
        if (pvCover.getLimits() != null && pvCover.getLimits().size() == 1) {
            return pvCover.getLimits().get(0);
        }

        if (sumInsured == null) {
            return null;
        }


        for (PvLimit pvLimit : pvCover.getLimits()) {
            if (Objects.equals(pvLimit.getSumInsured(), sumInsured)) {
                return pvLimit;
            }
        }
        return null;
    }

    public static PvDeductible getPvDeductible(PvCover pvCover, Cover policyCover) {
        // если франшиза обязательна и в списке только одно значение то берем его
        // если франшиза обязательна а щапросе не ередена ничего, то берем франшизу с минимальным номером
        // если чтото передано, то проверяем по списку что это значение есть
        Double deductible = policyCover.getDeductible();
        String deductibleType = policyCover.getDeductibleType();
        String deductibleSpecific = policyCover.getDeductibleSpecific();
        String deductibleUnit = policyCover.getDeductibleUnit();

        if (pvCover.getDeductibles() == null || pvCover.getDeductibles().isEmpty()) {
            return null;
        }
        for (PvDeductible pvDed : pvCover.getDeductibles()) {
            boolean deductibleMatch = deductible != null && deductible.equals(pvDed.getDeductible());
            boolean typeMatch = deductibleType != null && deductibleType.equals(pvDed.getDeductibleType());
            boolean specificMatch = deductibleSpecific != null && deductibleSpecific.equals(pvDed.getDeductibleSpecific());
            boolean unitMatch = deductibleUnit != null && deductibleUnit.equals(pvDed.getDeductibleUnit());
            if (deductibleMatch && typeMatch && specificMatch && unitMatch) {
                return pvDed;
            }
        }
        if (pvCover.getIsDeductibleMandatory()) {
            List<PvDeductible> deductibles = pvCover.getDeductibles();
            if (deductibles != null && !deductibles.isEmpty()) {
                deductibles.sort(java.util.Comparator.comparingInt(d -> {
                    // Try to get "nr" property, default to Integer.MAX_VALUE if not present or not a number
                    try {
                        java.lang.reflect.Method getNr = d.getClass().getMethod("getNr");
                        Object nrObj = getNr.invoke(d);
                        if (nrObj instanceof Number) {
                            return ((Number) nrObj).intValue();
                        } else if (nrObj != null) {
                            return Integer.parseInt(nrObj.toString());
                        }
                    } catch (Exception e) {
                        // ignore and use max value
                    }
                    return Integer.MAX_VALUE;
                }));
                return deductibles.get(0);
            }
        }

        return null;
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


}
