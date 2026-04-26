package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.service.ClientService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    private final ClientService clientService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("formTitle", "Nouveau client");
        return "clients/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable : " + id));
        model.addAttribute("client", client);
        model.addAttribute("formTitle", "Modifier le client");
        return "clients/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Client client,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", client.getId() == null ? "Nouveau client" : "Modifier le client");
            return "clients/form";
        }
        clientService.save(client);
        redirectAttrs.addFlashAttribute("successMessage", "Client enregistré avec succès.");
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        clientService.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Client supprimé.");
        return "redirect:/clients";
    }
}
