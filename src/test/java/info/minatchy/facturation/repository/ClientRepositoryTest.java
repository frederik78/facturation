package info.minatchy.facturation.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import info.minatchy.facturation.model.Client;

@DataJpaTest
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void saveAndFindAllOrdered() {
        Client a = new Client(); a.setName("B Company");
        Client b = new Client(); b.setName("A Company");
        clientRepository.save(a);
        clientRepository.save(b);

        List<Client> list = clientRepository.findAllByOrderByNameAsc();
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo("A Company");
    }
}
