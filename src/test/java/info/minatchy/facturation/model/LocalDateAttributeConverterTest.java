package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateAttributeConverterTest {

    private final LocalDateAttributeConverter conv = new LocalDateAttributeConverter();

    @Test
    void nullAndEmpty() {
        assertNull(conv.convertToDatabaseColumn(null));
        assertNull(conv.convertToEntityAttribute(null));
        assertNull(conv.convertToEntityAttribute(""));
    }

    @Test
    void epochMillis() {
        long now = Instant.now().toEpochMilli();
        LocalDate d = conv.convertToEntityAttribute(String.valueOf(now));
        assertNotNull(d);
    }

    @Test
    void isoDateAndDateTime() {
        LocalDate d1 = conv.convertToEntityAttribute("2026-04-28");
        assertEquals(LocalDate.of(2026,4,28), d1);

        LocalDate d2 = conv.convertToEntityAttribute("2026-04-28T15:30:00");
        assertEquals(LocalDate.of(2026,4,28), d2);
    }

    @Test
    void sqlPatternsAndPrefix() {
        LocalDate d = conv.convertToEntityAttribute("2026-04-28 12:00:00.000");
        assertEquals(LocalDate.of(2026,4,28), d);

        LocalDate dPref = conv.convertToEntityAttribute("2026-04-28T12:00:00.000Z[UTC]");
        assertEquals(LocalDate.of(2026,4,28), dPref);
    }

    @Test
    void invalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> conv.convertToEntityAttribute("not-a-date"));
    }
}
