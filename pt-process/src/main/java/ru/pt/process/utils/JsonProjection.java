package ru.pt.process.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.product.LobVar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO null -> Optional
public class JsonProjection {

    private final DocumentContext documentContext;

    public JsonProjection(String json) {
        this.documentContext = JsonPath.parse(json);
    }

    /**
     * Получить код продукта
     */
    public String getProductCode() {
        return documentContext.read("$.product.code", String.class);
    }

    public Integer getPackageCode() {
        try {
            return documentContext.read("$.insuredObject.packageCode", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public InsuredObject getInsuredObject() {
        try {
            return documentContext.read("$.insuredObject", InsuredObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public ZonedDateTime getIssueDate() {
        try {
            return ZonedDateTime.parse(documentContext.read("$.issueDate", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    public ZonedDateTime getStartDate() {
        try {
            return ZonedDateTime.parse(documentContext.read("$.startDate", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    public ZonedDateTime getEndDate() {
        try {
            return ZonedDateTime.parse(documentContext.read("$.endDate", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    public String getPolicyTerm() {
        try {
            return documentContext.read("$.policyTerm", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getWaitingPeriod() {
        try {
            return documentContext.read("$.waitingPeriod", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    // код переменной -> значение переменной
    public Map<String, Object> getProductMapFromRequest(List<LobVar> vars) {
        var result = new HashMap<String, Object>();
        vars.forEach(v -> {
            switch (v.getVarDataType()) {
                case STRING -> result.put(v.getVarCode(), documentContext.read(v.getVarPath(), String.class));
                case DATE -> result.put(v.getVarCode(), documentContext.read(v.getVarPath(), LocalDate.class));
                case TIME -> result.put(v.getVarCode(), documentContext.read(v.getVarPath(), LocalDateTime.class));
                case NUMBER -> result.put(v.getVarCode(), documentContext.read(v.getVarPath(), BigDecimal.class));
            }
        });
        return result;
    }

    public Map<String, Object> getProductMap(List<LobVar> vars) {
        Map<String, Object> mapVars = new HashMap<>();
        for (LobVar lobVar : vars) {
            mapVars.put(lobVar.getVarCode(), lobVar.getVarValue());
        }
        return mapVars;
    }

    public UUID getPolicyId() {
        return UUID.fromString(documentContext.read("$.draftId", String.class));
    }

    public String evaluateJsonPath(String path) {
        return documentContext.read(path, String.class);
    }
}
