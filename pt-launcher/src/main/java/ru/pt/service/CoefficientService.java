package ru.pt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pt.domain.CoefficientData;
import ru.pt.repository.CoefficientDataRepository;

import java.util.List;
import java.util.Map;

@Service
public class CoefficientService {

    private final CoefficientDataRepository repository;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public CoefficientService(CoefficientDataRepository repository, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void mapFromJson(CoefficientData entity, JsonNode json) {
        ArrayNode condition = (json.has("conditionValue") && json.get("conditionValue").isArray()) ? (ArrayNode) json.get("conditionValue") : objectMapper.createArrayNode();
        entity.setCol0(condition.size() > 0 ? condition.get(0).asText(null) : null);
        entity.setCol1(condition.size() > 1 ? condition.get(1).asText(null) : null);
        entity.setCol2(condition.size() > 2 ? condition.get(2).asText(null) : null);
        entity.setCol3(condition.size() > 3 ? condition.get(3).asText(null) : null);
        entity.setCol4(condition.size() > 4 ? condition.get(4).asText(null) : null);
        entity.setCol5(condition.size() > 5 ? condition.get(5).asText(null) : null);
        entity.setCol6(condition.size() > 6 ? condition.get(6).asText(null) : null);
        entity.setCol7(condition.size() > 7 ? condition.get(7).asText(null) : null);
        entity.setCol8(condition.size() > 8 ? condition.get(8).asText(null) : null);
        entity.setCol9(condition.size() > 9 ? condition.get(9).asText(null) : null);
        entity.setCol10(condition.size() > 10 ? condition.get(10).asText(null) : null);
        if (json.has("resultValue") && !json.get("resultValue").isNull()) {
            entity.setResultValue(json.get("resultValue").asDouble());
        } else {
            entity.setResultValue(null);
        }
    }

    private ObjectNode mapToJson(CoefficientData entity) {
        ObjectNode row = objectMapper.createObjectNode();
        row.put("id", entity.getId());
        ArrayNode cond = objectMapper.createArrayNode();
        cond.add(entity.getCol0());
        cond.add(entity.getCol1());
        cond.add(entity.getCol2());
        cond.add(entity.getCol3());
        cond.add(entity.getCol4());
        cond.add(entity.getCol5());
        cond.add(entity.getCol6());
        cond.add(entity.getCol7());
        cond.add(entity.getCol8());
        cond.add(entity.getCol9());
        cond.add(entity.getCol10());
        row.set("conditionValue", cond);
        if (entity.getResultValue() != null) row.put("resultValue", entity.getResultValue());
        return row;
    }

    @Transactional
    public CoefficientData insert(Integer calculatorId, String code, JsonNode coefficientDataJson) {
        CoefficientData entity = new CoefficientData();
        entity.setCalculatorId(calculatorId);
        entity.setCoefficientCode(code);
        mapFromJson(entity, coefficientDataJson);
        return repository.save(entity);
    }

    @Transactional
    public CoefficientData update(Integer id, JsonNode coefficientDataJson) {
        CoefficientData entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Coefficient row not found: " + id));
        mapFromJson(entity, coefficientDataJson);
        return repository.save(entity);
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ArrayNode getTable(Integer calculatorId, String code) {
        List<CoefficientData> rows = repository.findAllByCalcAndCode(calculatorId, code);
        ArrayNode data = objectMapper.createArrayNode();
        for (CoefficientData e : rows) {
            data.add(mapToJson(e));
        }
        return data;
//        ObjectNode table = objectMapper.createObjectNode();
//        table.set("data", data);
//        return table;
    }

    @Transactional
    public ArrayNode replaceTable(Integer calculatorId, String code, ArrayNode tableJson) {
        repository.deleteAllByCalcAndCode(calculatorId, code);
        //ArrayNode data = (tableJson.has("data") && tableJson.get("data").isArray()) ? (ArrayNode) tableJson.get("data") : objectMapper.createArrayNode();
        for (JsonNode row : tableJson) {
            insert(calculatorId, code, row);
        }
        return getTable(calculatorId, code);
    }

    @Transactional(readOnly = true)
    public String getCoefficientValue(Integer calculatorId,
                                      String coefficientCode,
                                      Map<String, String> values,
                                      List<ru.pt.domain.calculator.CoefficientColumn> columns) {
        if (calculatorId == null || coefficientCode == null || columns == null) {
            return null;
        }

        StringBuilder sql = new StringBuilder("select result_value from coefficient_data where calculator_id = ");
        sql.append(calculatorId.toString());
        sql.append(" and coefficient_code = ");
        sql.append("'").append(coefficientCode).append("'");

        StringBuilder orderBy = new StringBuilder();
        java.util.List<Object> params = new java.util.ArrayList<>();
        params.add(calculatorId.toString());
        params.add(coefficientCode);

        for (ru.pt.domain.calculator.CoefficientColumn col : columns) {
            if (col == null) continue;
            String varCode = col.getVarCode();
            String nr = (col.getNr() - 1) + "";  // TODO
            String op = col.getConditionOperator();
            String sortOrder = col.getSortOrder();
            String varDataType = col.getVarDataType();

            if (varCode == null || nr == null || op == null) return null;
            if (!nr.matches("1?0|[0-9]")) return null; // only 0..10

            String varValue = values != null ? values.get(varCode) : null;
            if (varValue == null) return null;

            String operator = normalizeOperator(op);
            if (operator == null) return null;

            if (varDataType.equals("NUMBER")) {
                sql.append(" AND to_number(col").append(nr).append(",'9999999999.99') ").append(operator).append(varValue);
            } else {
                sql.append(" AND col").append(nr).append(" ").append(operator).append("'").append(varValue).append("'");
            }
            params.add(varValue);

            String ord = normalizeOrder(sortOrder);
            if (ord != null) {
                if (orderBy.length() == 0) orderBy.append(" order by "); else orderBy.append(", ");
                orderBy.append("col").append(nr).append(" ").append(ord);
            }
        }

        if (orderBy.length() > 0) sql.append(orderBy);
        sql.append(" limit 1");
        String sqlS = sql.toString();

        try {
            Double result = jdbcTemplate.query(sqlS, rs -> rs.next() ? rs.getDouble(1) : null);
            return result == null ? null : String.valueOf(result);
        } catch (Exception e) {
            return null;
        }
    }

    private String normalizeOperator(String op) {
        if (op == null) return null;
        String s = op.trim().toUpperCase();
        return switch (s) {
            case "=", ">", "<", ">=", "<=", "<>" -> s;
            case "LIKE" -> "LIKE";
            default -> null;
        };
    }

    private String normalizeOrder(String order) {
        if (order == null) return null;
        String s = order.trim().toUpperCase();
        return switch (s) {
            case "ASC", "DESC" -> s;
            default -> null;
        };
    }
}


