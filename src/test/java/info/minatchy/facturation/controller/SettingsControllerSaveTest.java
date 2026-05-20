package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.IssuerService;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsControllerSaveTest {

    @Test
    void saveRedirectsAndAddsFlash() {
        AtomicReference<Issuer> saved = new AtomicReference<>();
        IssuerService stub = new IssuerService(null) {
            @Override
            public Issuer save(Issuer issuer) { saved.set(issuer); return issuer; }
        };

        SettingsController ctrl = new SettingsController(stub);
        Issuer toSave = new Issuer(); toSave.setCompanyName("C");
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();

        String res = ctrl.save(toSave, attrs);
        assertEquals("redirect:/settings", res);
        assertNotNull(saved.get());
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }
}
