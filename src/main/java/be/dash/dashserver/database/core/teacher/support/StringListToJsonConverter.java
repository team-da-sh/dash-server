package be.dash.dashserver.database.core.teacher.support;

import java.util.Collections;
import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.dash.dashserver.core.exception.PersistenceException;
import lombok.RequiredArgsConstructor;

@Converter
@Component
@RequiredArgsConstructor
public class StringListToJsonConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new PersistenceException("직렬화에 실패했습니다. attribute: " + attribute);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new PersistenceException("역직렬화에 실패했습니다. dbData: " + dbData);
        }
    }
}
