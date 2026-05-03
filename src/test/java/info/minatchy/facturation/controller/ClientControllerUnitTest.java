package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ClientControllerUnitTest {

    @Test
    void listAddsClients() {
        ClientService svc = new ClientService(null) {
            @Override public java.util.List<Client> findAll() { return List.of(new Client()); }
        };

        ClientController ctrl = new ClientController(svc);
        Model model = new ConcurrentModel();
        String view = ctrl.list(model);
        assertEquals("clients/list", view);
        assertTrue(model.containsAttribute("clients"));
    }

    @Test
    void newFormProvidesEmptyClient() {
        ClientService svc = new ClientService(null) {};
        ClientController ctrl = new ClientController(svc);
        Model model = new ConcurrentModel();
        String view = ctrl.newForm(model);
        assertEquals("clients/form", view);
        assertTrue(model.containsAttribute("client"));
        assertEquals("Nouveau client", model.getAttribute("formTitle"));
    }

    @Test
    void editFormFoundAndNotFound() {
        Client c = new Client(); c.setId(7L); c.setName("X");
        ClientService svc = new ClientService(null) {
            @Override public java.util.Optional<Client> findById(Long id) { return Optional.of(c); }
        };
        ClientController ctrl = new ClientController(svc);
        Model model = new ConcurrentModel();
        String view = ctrl.editForm(7L, model);
        assertEquals("clients/form", view);
        assertTrue(model.containsAttribute("client"));

        ClientService missing = new ClientService(null) {
            @Override public java.util.Optional<Client> findById(Long id) { return Optional.empty(); }
        };
        ClientController ctrl2 = new ClientController(missing);
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> ctrl2.editForm(1L, new ConcurrentModel()));
    }

    @Test
    void saveWithValidationErrorReturnsForm() {
        ClientService svc = new ClientService(null) {};
        ClientController ctrl = new ClientController(svc);

        Client client = new Client(); client.setName("");
        BindingResult errors = new BeanPropertyBindingResult(client, "client");
        errors.reject("name", "required");
        Model model = new ConcurrentModel();
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();

        String view = ctrl.save(client, errors, model, attrs);
        assertEquals("clients/form", view);
        assertTrue(model.containsAttribute("formTitle"));
    }

    @Test
    void saveSuccessRedirectsAndDeletes() {
        AtomicBoolean saved = new AtomicBoolean(false);
        ClientService svc = new ClientService(null) {
            @Override public Client save(Client client) { client.setId(42L); saved.set(true); return client; }
        };
        ClientController ctrl = new ClientController(svc);

        Client client = new Client(); client.setName("Name");
        BindingResult errors = new BeanPropertyBindingResult(client, "client");
        Model model = new ConcurrentModel();
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();

        String view = ctrl.save(client, errors, model, attrs);
        assertEquals("redirect:/clients", view);
        assertTrue(saved.get());
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));

        // delete
        AtomicBoolean deleted = new AtomicBoolean(false);
        ClientService svcDel = new ClientService(null) { @Override public void delete(Long id) { deleted.set(true); } };
        ClientController ctrlDel = new ClientController(svcDel);
        RedirectAttributesModelMap delAttrs = new RedirectAttributesModelMap();
        String delView = ctrlDel.delete(5L, delAttrs);
        assertEquals("redirect:/clients", delView);
        assertTrue(delAttrs.getFlashAttributes().containsKey("successMessage"));
    }
}
