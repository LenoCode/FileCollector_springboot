package com.leno.example.domain.mappers.general;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Mapper that maps column that are type of Json in database into HashMap object.
 * It is used for example in DocumentCollectorDao in column collector_info
 */
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Convert map to string
     * @param attribute  the entity attribute value to be converted
     * @return
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        String stringJson = null;
        try {
            stringJson = objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
        }

        return stringJson;
    }

    /**
     * Convert string to map
     * @param dbData  the data from the database column to be
     *                converted
     * @return
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(dbData, Map.class);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            return new HashMap<>();
        }
        return map;
    }
}
