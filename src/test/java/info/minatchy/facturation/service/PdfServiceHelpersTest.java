package info.minatchy.facturation.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceHelpersTest {

    private final PdfService pdf = new PdfService();

    @Test
    void formatCurrencyAndQuantityNulls() throws Exception {
        Method fmtCur = PdfService.class.getDeclaredMethod("formatCurrency", BigDecimal.class);
        fmtCur.setAccessible(true);
        Object res = fmtCur.invoke(pdf, new Object[]{ (BigDecimal) null });
        assertEquals("0,00", res);

        Method fmtQty = PdfService.class.getDeclaredMethod("formatQuantity", BigDecimal.class);
        fmtQty.setAccessible(true);
        Object res2 = fmtQty.invoke(pdf, new Object[]{ (BigDecimal) null });
        assertEquals("0", res2);
    }
}
