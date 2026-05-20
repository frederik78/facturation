package info.minatchy.facturation.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import info.minatchy.facturation.model.Issuer;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class IssuerRepositoryTest {

    @Autowired
    private IssuerRepository issuerRepository;

    @Test
    void saveAndFindIssuer() {
        Issuer issuer = new Issuer();
        issuer.setCompanyName("ACME SA");
        issuer.setContactName("John Doe");
        issuer.setEmail("john@example.com");
        issuer.setPhone("+1 234 567 890");

        Issuer saved = issuerRepository.save(issuer);
        assertThat(saved.getId()).isNotNull();

        Optional<Issuer> found = issuerRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCompanyName()).isEqualTo("ACME SA");
    }

    @Test
    void findAllAndDelete() {
        Issuer a = new Issuer(); a.setCompanyName("A Co"); a.setContactName("A");
        Issuer b = new Issuer(); b.setCompanyName("B Co"); b.setContactName("B");
        issuerRepository.save(a);
        issuerRepository.save(b);

        List<Issuer> all = issuerRepository.findAll();
        assertThat(all).hasSize(2);

        issuerRepository.deleteAll();
        assertThat(issuerRepository.findAll()).isEmpty();
    }
}
