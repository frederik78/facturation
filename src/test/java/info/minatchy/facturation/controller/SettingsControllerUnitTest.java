package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.IssuerService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsControllerUnitTest {

    @Test
    void formShowsIssuer() {
        Issuer issuer = new Issuer(); issuer.setCompanyName("Co"); issuer.setContactName("P");
        IssuerService stub = new IssuerService(null) {
            @Override
            public Optional<Issuer> getIssuer() { return Optional.of(issuer); }
        };

        SettingsController ctrl = new SettingsController(stub);
        Model model = new ConcurrentModel();
        String view = ctrl.form(model);
        assertEquals("settings/form", view);
        assertTrue(model.containsAttribute("issuer"));
        assertSame(issuer, model.getAttribute("issuer"));
    }
}
