package ru.pt.process.utils;

import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.ValidatorRule;
import ru.pt.api.dto.product.VarDataType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ValidatorImpl {


    private static boolean checkString(String type, String v1, String v2) {

        switch (type) {
            case "NOT_NULL":
                return v1 != null && !v1.isEmpty();
            case "=":
                return Objects.equals(v1, v2);
            case "!=":
                return !Objects.equals(v1, v2);
            case "MATCHES_REGEX":
                return v1 != null && v2 != null && v1.matches(v2);
            case "IN_LIST":
                if (v2 == null) return false;
                String[] items = v2.split(",");
                for (String item : items) {
                    if (Objects.equals(v1, item.trim())) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private static boolean checkNumber(String type, String s1, String s2) {

        switch (type) {
            case "=":
                return Double.parseDouble(s1) == Double.parseDouble(s2);
            case "!=":
                return Double.parseDouble(s1) != Double.parseDouble(s2);
            case ">":
                return Double.parseDouble(s1) > Double.parseDouble(s2);
            case "<":
                return Double.parseDouble(s1) < Double.parseDouble(s2);
            case ">=":
                return Double.parseDouble(s1) >= Double.parseDouble(s2);
            case "<=":
                return Double.parseDouble(s1) <= Double.parseDouble(s2);
            case "RANGE":
                String[] rightParts = s2.split("-");

                if (Double.parseDouble(s1) >= Double.parseDouble(rightParts[0].trim()) && Double.parseDouble(s1) <= Double.parseDouble(rightParts[1].trim())) {
                    return true;
                }

                return false;
            default:
                return false;
        }
    }

    public static boolean validate(
            Map<String, LobVar> dataMap,
            ValidatorRule validatorRule
    ) {
        String leftKey = validatorRule.getKeyLeft();
        String rightKey = validatorRule.getKeyRight();
        String rightValue = validatorRule.getValueRight();
        String ruleType = validatorRule.getRuleType();
        try {
            // TODO медленно
            LobVar leftVarDef = dataMap.get(leftKey);
            LobVar rightVarDef = dataMap.get(rightKey);
            if (rightVarDef != null) {
                if (!leftVarDef.getVarType().equalsIgnoreCase(rightVarDef.getVarType())) {
                    return false;
                }
            }
            if (rightVarDef == null) {
                rightVarDef = new LobVar(rightKey, "", "", leftVarDef.getVarType(), rightValue, leftVarDef.getVarDataType());
            }

            if (leftVarDef == null || rightVarDef == null){
                return false;
            }
            if (leftVarDef.getVarDataType() == VarDataType.NUMBER) {
                return checkNumber(ruleType, leftVarDef.getVarValue(), rightVarDef.getVarValue());
            }
            if (leftVarDef.getVarDataType() == VarDataType.STRING) {
                return checkString(ruleType, leftVarDef.getVarValue(), rightVarDef.getVarValue());
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
