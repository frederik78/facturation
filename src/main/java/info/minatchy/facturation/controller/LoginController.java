package info.minatchy.facturation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import info.minatchy.facturation.service.LoginService;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login() {
        // Si l'utilisateur est déjà authentifié, on le redirige vers l'accueil
        // pour éviter qu'il ne se reconnecte inutilement.
        if (loginService.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    /**
     * Note : La méthode POST /login n'est pas nécessaire ici.
     * Elle est gérée automatiquement par le filtre UsernamePasswordAuthenticationFilter
     * de Spring Security, qui utilise les paramètres 'username' et 'password'
     * envoyés par votre formulaire HTML.
     */
}
