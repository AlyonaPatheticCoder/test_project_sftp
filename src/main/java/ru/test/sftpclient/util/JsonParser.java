package ru.test.sftpclient.util;

import ru.test.sftpclient.model.DomainIp;

import java.util.ArrayList;
import java.util.List;

public final class JsonParser {

    private static final String ADDRESSES = "\"addresses\"";
    private static final String DOMAIN = "\"domain\"";
    private static final String IP = "\"ip\"";

    private JsonParser() {
    }

    public static List<DomainIp> parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("Json is empty");
        }

        String content = json.trim();

        int arrayStart = content.indexOf('[', content.indexOf(ADDRESSES));
        int arrayEnd = content.lastIndexOf(']');

        if (arrayStart < 0 || arrayEnd < arrayStart) {
            throw new IllegalArgumentException("Invalid json structure");
        }

        String array = content.substring(arrayStart + 1, arrayEnd).trim();

        List<DomainIp> result = new ArrayList<>();

        if (content.isEmpty()) {
            return result;
        }

        for (String object : array.split("\\},\\s*\\{")) {
            object = object.replace("{", "").replace("}", "");

            String domain = extractValue(object, DOMAIN);
            String ip = extractValue(object, IP);

            result.add(new DomainIp(domain, ip));
        }

        return result;
    }

    private static String extractValue(String object, String field) {
        int start = object.indexOf(field);

        if (start < 0) {
            throw new IllegalArgumentException(
                    "Missing field: " + field
            );
        }

        start = object.indexOf('"', start + field.length()) + 1;
        int end = object.indexOf('"', start);

        if (start == 0 || end < 0) {
            throw new IllegalArgumentException(
                    "Invalid value for field: " + field
            );
        }

        return object.substring(start, end);
    }

    public static String toJson(List<DomainIp> pairs) {
        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("  \"addresses\": [\n");

        for (int i = 0; i < pairs.size(); i++) {
            DomainIp pair = pairs.get(i);

            json.append("    {")
                    .append("\"domain\":\"")
                    .append(escape(pair.getDomain()))
                    .append("\", ")
                    .append("\"ip\":\"")
                    .append(escape(pair.getIp()))
                    .append("\"}");

            if (i < pairs.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}");

        return json.toString();
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}