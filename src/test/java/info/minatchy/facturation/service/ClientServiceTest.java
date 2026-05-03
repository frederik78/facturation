package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void findAllDelegates() {
        when(clientRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList(new Client(), new Client()));
        List<Client> all = clientService.findAll();
        assertThat(all).hasSize(2);
        verify(clientRepository).findAllByOrderByNameAsc();
    }

    @Test
    void saveAndDelete() {
        Client c = new Client(); c.setName("C");
        when(clientRepository.save(c)).thenReturn(c);
        Client saved = clientService.save(c);
        assertThat(saved).isEqualTo(c);

        doNothing().when(clientRepository).deleteById(1L);
        clientService.delete(1L);
        verify(clientRepository).deleteById(1L);
    }
}
