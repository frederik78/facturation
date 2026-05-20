package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.repository.IssuerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssuerServiceTest {

    @Mock
    private IssuerRepository issuerRepository;

    @InjectMocks
    private IssuerService issuerService;

    @Test
    void getIssuerWhenPresent() {
        Issuer i = new Issuer(); i.setCompanyName("X"); i.setContactName("Y");
        when(issuerRepository.findAll()).thenReturn(Collections.singletonList(i));

        Optional<Issuer> res = issuerService.getIssuer();
        assertThat(res).isPresent();
        verify(issuerRepository, times(1)).findAll();
    }

    @Test
    void saveDelegatesToRepo() {
        Issuer i = new Issuer(); i.setCompanyName("A"); i.setContactName("B");
        when(issuerRepository.save(i)).thenReturn(i);
        Issuer saved = issuerService.save(i);
        assertThat(saved).isEqualTo(i);
        verify(issuerRepository).save(i);
    }
}
