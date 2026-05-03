package info.minatchy.facturation.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import info.minatchy.facturation.model.Issuer;

public class IssuerServiceNoRepoTest {



    @Test
    void getIssuerFindsFirst() {
        IssuerService svc = new IssuerService(info.minatchy.facturation.TestRepoFactory.issuerRepo(1, List.of(new Issuer())));
        Optional<Issuer> res = svc.getIssuer();
        assertTrue(res.isPresent());
    }
}
