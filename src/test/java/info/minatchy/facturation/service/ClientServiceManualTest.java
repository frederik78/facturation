package info.minatchy.facturation.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.repository.ClientRepository;

public class ClientServiceManualTest {



    @Test
    void basicCrudViaService() {
        ClientRepository repo = info.minatchy.facturation.TestRepoFactory.clientRepoDefault();
        ClientService svc = new ClientService(repo);

        List<Client> all = svc.findAll();
        assertFalse(all.isEmpty());

        Client toSave = new Client(); toSave.setName("X");
        Client saved = svc.save(toSave);
        assertEquals(5L, saved.getId());

        svc.delete(123L);
        // delete doesn't expose state; ensure no exception thrown
    }
}
