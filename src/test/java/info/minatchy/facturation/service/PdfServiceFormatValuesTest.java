package info.minatchy.facturation.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceFormatValuesTest {

    private final PdfService pdf = new PdfService();

    @Test
    void formatCurrencyAndQuantityNonNull() throws Exception {
        Method fmtCur = PdfService.class.getDeclaredMethod("formatCurrency", BigDecimal.class);
        fmtCur.setAccessible(true);
        Object res = fmtCur.invoke(pdf, new BigDecimal("1234.5"));
        String s = (String) res;
        assertTrue(s.contains(",")); // decimal comma
        assertTrue(s.endsWith("50") || s.endsWith("50\u00A0"));

        Method fmtQty = PdfService.class.getDeclaredMethod("formatQuantity", BigDecimal.class);
        fmtQty.setAccessible(true);
        Object res2 = fmtQty.invoke(pdf, new BigDecimal("24.50"));
        assertNotNull(res2);
        String q = (String) res2;
        assertTrue(q.startsWith("24"));
    }
}
