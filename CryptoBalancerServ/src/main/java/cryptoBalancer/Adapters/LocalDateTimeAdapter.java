package cryptoBalancer.Adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Адаптер для LocalDateTime
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Используем стандартный формат ISO
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Преобразуем LocalDateTime в строку
        return new JsonPrimitive(FORMATTER.format(src));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Преобразуем строку обратно в LocalDateTime
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}
