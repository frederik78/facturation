package info.minatchy.facturation.controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceControllerHelpersTest {

    private final InvoiceController ctrl = new InvoiceController(null, null, null, null);

    @Test
    void getOrNullAndGetOrDefaultBehavior() throws Exception {
        Method getOrNull = InvoiceController.class.getDeclaredMethod("getOrNull", java.util.List.class, int.class);
        getOrNull.setAccessible(true);

        Object r1 = getOrNull.invoke(ctrl, null, 0);
        assertNull(r1);

        Object r2 = getOrNull.invoke(ctrl, List.of(""), 0);
        assertNull(r2);

        Method getOrDefault = InvoiceController.class.getDeclaredMethod("getOrDefault", java.util.List.class, int.class, String.class);
        getOrDefault.setAccessible(true);

        Object d1 = getOrDefault.invoke(ctrl, null, 0, "def");
        assertEquals("def", d1);

        Object d2 = getOrDefault.invoke(ctrl, List.of(" "), 0, "x");
        assertEquals("x", d2);
    }

    @Test
    void parseBigDecimalVariousInputs() throws Exception {
        Method parse = InvoiceController.class.getDeclaredMethod("parseBigDecimal", String.class);
        parse.setAccessible(true);

        Object n1 = parse.invoke(ctrl, "1,23");
        assertEquals(new BigDecimal("1.23"), n1);

        Object n2 = parse.invoke(ctrl, "bad");
        assertEquals(BigDecimal.ZERO, n2);

        Object n3 = parse.invoke(ctrl, (Object) null);
        assertEquals(BigDecimal.ZERO, n3);
    }
}
