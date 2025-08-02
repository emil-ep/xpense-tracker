package com.xperia.xpense_tracker.models.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try{
            return objectMapper.writeValueAsString(jsonNode);
        }catch (Exception ex){
            throw new IllegalArgumentException("Error serializing JsonNode to JSON", ex);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String s) {
        try{
            return objectMapper.readTree(s);
        }catch (Exception ex){
            throw new IllegalArgumentException("Error deserializing JSON to JsonNode", ex);
        }
    }
}
