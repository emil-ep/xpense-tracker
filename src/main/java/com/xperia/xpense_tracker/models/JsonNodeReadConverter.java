package com.xperia.xpense_tracker.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

@Converter(autoApply = true)
public class JsonNodeReadConverter implements AttributeConverter<JsonNode, Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(JsonNode jsonNode) {
        return jsonNode;
    }

    @Override
    public JsonNode convertToEntityAttribute(Object o) {
        if (o == null) return null;

        try {
            if (o instanceof PGobject pgObject) {
                return objectMapper.readTree(pgObject.getValue());
            }
            return objectMapper.readTree(o.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting PGobject to JsonNode", e);
        }
    }
}
