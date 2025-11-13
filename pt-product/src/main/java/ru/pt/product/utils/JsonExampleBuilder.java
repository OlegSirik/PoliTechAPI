package ru.pt.product.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonExampleBuilder {


    public static String buildJsonExample(List<String> jsonPaths) throws Exception {
        return buildCompleteJson(jsonPaths, createPathsMap());
    }

    public static String buildJsonExampleProduct(List<String> jsonPaths, Map<String, String> values) throws Exception {
        Map<String, String> combined = new HashMap<>(createPathsMap());
        if (values != null) {
            combined.putAll(values);
        }
        return buildCompleteJson(jsonPaths, combined);
    }

    public static String buildCompleteJson(List<String> jsonPaths, Map<String, String> values) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        // Сначала создаем структуру
        for (String path : jsonPaths) {
            String value = values.get(path);
            if (value == null) {
                value = "";
            }
            createPath(root, path, value);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    private static void createPath(ObjectNode node, String jsonPath, String value) {
        String[] parts = jsonPath.split("\\.");
        ObjectNode current = node;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.contains("[")) {
                String arrayName = part.substring(0, part.indexOf("["));
                int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));

                if (!current.has(arrayName)) {
                    current.set(arrayName, new ObjectMapper().createArrayNode());
                }

                ArrayNode array = (ArrayNode) current.get(arrayName);
                while (array.size() <= index) {
                    array.addObject();
                }

                current = (ObjectNode) array.get(index);
            } else {
                if (!current.has(part)) {
                    if (i < parts.length - 1) {
                        current.set(part, new ObjectMapper().createObjectNode());
                    } else {
                        current.put(part, value);
                    }
                }
                if (i < parts.length - 1) {
                    current = (ObjectNode) current.get(part);
                }
            }
        }
    }

    private static Map<String, String> createPathsMap() {
        Map<String, String> paths = new LinkedHashMap<>();

        // Person paths
        paths.put("policyHolder.person.firstName", "Фаддей");
        paths.put("policyHolder.person.lastName", "Беллинсгаузен");
        paths.put("policyHolder.person.middleName", "Фаддеевич");
        paths.put("policyHolder.person.birthDate", "1779-08-18");
        paths.put("policyHolder.person.fullName", "Bellinghausen, Faddey Feddeevich");
        paths.put("policyHolder.person.fullNameEn", "Bellinghausen, Faddey Feddeevich");
        paths.put("policyHolder.person.birthPlace", "Санкт-Петербург");
        paths.put("policyHolder.person.citizenship", "RU");
        paths.put("policyHolder.person.gender", "MALE");
        paths.put("policyHolder.person.familyState", "SINGLE");
        paths.put("policyHolder.person.isPublicOfficial", "false");
        paths.put("policyHolder.person.isResident", "true");
        paths.put("policyHolder.person.vsk_id", "1234567890");
        paths.put("policyHolder.person.ext_id", "");

        // Phone paths
        paths.put("policyHolder.phone.phoneNumber", "79991234567");

        // Email paths
        paths.put("policyHolder.email", "faddey.bellinghausen@example.com");

        // Organization paths
        paths.put("policyHolder.organization.country", "");
        paths.put("policyHolder.organization.inn", "");
        paths.put("policyHolder.organization.fullName", "");
        paths.put("policyHolder.organization.fullNameEn", "");
        paths.put("policyHolder.organization.shortName", "");
        paths.put("policyHolder.organization.legalForm", "");
        paths.put("policyHolder.organization.kpp", "");
        paths.put("policyHolder.organization.ogrn", "");
        paths.put("policyHolder.organization.okpo", "");
        paths.put("policyHolder.organization.bic", "");
        paths.put("policyHolder.organization.isResident", "true");
        paths.put("policyHolder.organization.group", "");
        paths.put("policyHolder.organization.vsk_id", "");
        paths.put("policyHolder.organization.ext_id", "");
        paths.put("policyHolder.organization.nciCode", "");

        // Document paths (Person)
        paths.put("policyHolder.passport.typeCode", "PASSPORT");
        paths.put("policyHolder.passport.serial", "1234");
        paths.put("policyHolder.passport.number", "765432");
        paths.put("policyHolder.passport.dateIssue", "1779-08-18");
        paths.put("policyHolder.passport.validUntil", "2029-08-18");
        paths.put("policyHolder.passport.whom", "Bellinghausen, Faddey Feddeevich");
        paths.put("policyHolder.passport.divisionCode", "12-34");
        paths.put("policyHolder.passport.vsk_id", "");
        paths.put("policyHolder.passport.ext_id", "");
        paths.put("policyHolder.passport.countryCode", "RU");

        // Address paths
        paths.put("policyHolder.address.typeCode", "REGISTRATION");
        paths.put("policyHolder.address.countryCode", "RU");
        paths.put("policyHolder.address.region", "");
        paths.put("policyHolder.address.city", "");
        paths.put("policyHolder.address.street", "");
        paths.put("policyHolder.address.house", "");
        paths.put("policyHolder.address.building", "");
        paths.put("policyHolder.address.flat", "");
        paths.put("policyHolder.address.room", "");
        paths.put("policyHolder.address.zipCode", "");
        paths.put("policyHolder.address.kladrId", "");
        paths.put("policyHolder.address.fiasId", "");
        paths.put("policyHolder.address.addressStr", "Санкт-Петербурга, Площадь Беллинсгаузена");
        paths.put("policyHolder.address.addressStrEn", "Saint Petersburg, Square Bellinghausen");
        paths.put("policyHolder.address.vsk_id", "");
        paths.put("policyHolder.address.ext_id", "");

        // Organization Document paths
        paths.put("policyHolder.document.typeCode", "");
        paths.put("policyHolder.document.serial", "");
        paths.put("policyHolder.document.number", "");
        paths.put("policyHolder.document.dateIssue", "");
        paths.put("policyHolder.document.validUntil", "");
        paths.put("policyHolder.document.whom", "");
        paths.put("policyHolder.document.divisionCode", "");
        paths.put("policyHolder.document.vsk_id", "");
        paths.put("policyHolder.document.ext_id", "");
        paths.put("policyHolder.document.countryCode", "RU");

        return Collections.unmodifiableMap(paths);
    }
}
