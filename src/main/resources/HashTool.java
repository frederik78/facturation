package info.minatchy.facturation.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashTool {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // Affiche le hash exact pour 'changeit'
        System.out.println("{bcrypt}" + encoder.encode("changeit"));
    }
}