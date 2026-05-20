package info.minatchy.facturation.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.repository.ClientRepository;

public class ClientServiceFindersTest {



    @Test
    void findAllFindByIdSaveDelete() {
        ClientRepository repo = info.minatchy.facturation.TestRepoFactory.clientRepoDefault();
        ClientService svc = new ClientService(repo);
        assertFalse(svc.findAll().isEmpty());
        Optional<Client> c = svc.findById(9L);
        assertTrue(c.isPresent());
        Client saved = svc.save(new Client());
        assertNotNull(saved);
        svc.delete(9L);
    }
}
