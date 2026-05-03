package info.minatchy.facturation.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.IssuerService;

@WebMvcTest(controllers = SettingsController.class)
public class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IssuerService issuerService;

    @Test
    void getFormShowsSettings() throws Exception {
        when(issuerService.getIssuer()).thenReturn(Optional.of(new Issuer()));
        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/form"));
    }
}
