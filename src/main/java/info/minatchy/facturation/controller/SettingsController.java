package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.IssuerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    public SettingsController(IssuerService issuerService) {
        this.issuerService = issuerService;
    }

    private final IssuerService issuerService;

    @GetMapping
    public String form(Model model) {
        Issuer issuer = issuerService.getIssuer().orElse(new Issuer());
        model.addAttribute("issuer", issuer);
        return "settings/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Issuer issuer, RedirectAttributes redirectAttrs) {
        issuerService.save(issuer);
        redirectAttrs.addFlashAttribute("successMessage", "Profil émetteur enregistré.");
        return "redirect:/settings";
    }
}
