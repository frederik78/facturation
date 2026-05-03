package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateAttributeConverterRoundTripTest {

    private final LocalDateAttributeConverter conv = new LocalDateAttributeConverter();

    @Test
    void convertToDatabaseColumnAndBack() {
        LocalDate d = LocalDate.of(2026,4,28);
        String db = conv.convertToDatabaseColumn(d);
        assertEquals("2026-04-28", db);
        LocalDate out = conv.convertToEntityAttribute(db);
        assertEquals(d, out);
    }
}
