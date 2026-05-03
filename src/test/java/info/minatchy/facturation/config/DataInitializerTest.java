package info.minatchy.facturation.config;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.repository.IssuerRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DataInitializerTest {



    @Test
    void runDoesNotThrowWhenNoIssuer() {
        IssuerRepository repo = info.minatchy.facturation.TestRepoFactory.issuerRepo(0);
        DataInitializer init = new DataInitializer(repo);
        assertDoesNotThrow(() -> init.run());
    }

    @Test
    void runDoesNotThrowWhenIssuerExists() {
        IssuerRepository repo = info.minatchy.facturation.TestRepoFactory.issuerRepo(1, List.of(new Issuer()));
        DataInitializer init = new DataInitializer(repo);
        assertDoesNotThrow(() -> init.run());
    }
}
