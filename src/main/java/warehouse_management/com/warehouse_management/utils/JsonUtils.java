package warehouse_management.com.warehouse_management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
        // private constructor để tránh tạo instance
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // parse JSON string -> Map
    public static Map<String, Object> parseJsonPrint(String jsonPrint) {
        try {
            return MAPPER.readValue(jsonPrint, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw LogicErrException.of("Failed to parse jsonPrint: " + e.getMessage());
        }
    }

    // serialize object -> JSON string
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw LogicErrException.of("Failed to convert object to JSON: " + e.getMessage());
        }
    }
    public static  <T> T parseJsonToDto(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException("JSON string is null or empty");
            }
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to DTO: " + e.getMessage(), e);
        }
    }
}

