package info.minatchy.facturation.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        String s = dbData.trim();

        // If value is numeric (epoch millis), convert to LocalDate
        if (s.matches("\\d+")) {
            try {
                long ms = Long.parseLong(s);
                return java.time.Instant.ofEpochMilli(ms)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
            } catch (NumberFormatException ignored) {
                // fall through to other parsers
            }
        }

        // Try ISO local date (yyyy-MM-dd)
        try {
            return LocalDate.parse(s);
        } catch (java.time.format.DateTimeParseException ignored) {
            // try next formats
        }

        // Try ISO local date-time
        try {
            java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(s);
            return ldt.toLocalDate();
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        // Try common SQL datetime patterns
        String[] patterns = new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss"};
        for (String p : patterns) {
            try {
                java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern(p);
                java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(s, f);
                return ldt.toLocalDate();
            } catch (java.time.format.DateTimeParseException ignored) {
            }
        }

        // As a last resort try the date prefix
        if (s.length() >= 10) {
            String prefix = s.substring(0, 10);
            try { return LocalDate.parse(prefix); } catch (java.time.format.DateTimeParseException ignored) {}
        }

        throw new IllegalArgumentException("Cannot convert database value to LocalDate: " + dbData);
    }
}
