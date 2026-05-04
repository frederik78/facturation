package info.minatchy.facturation.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ouvre un second connecteur Tomcat sur le port HTTP (8080 par défaut) qui
 * redirige toutes les requêtes vers le port HTTPS (8443 par défaut).
 *
 * La redirection est gérée par Spring Security (.requiresSecure()) qui renvoie
 * un HTTP 302 vers https://host:8443/... pour toute requête entrante sur le
 * connecteur HTTP.
 */
@Configuration
public class HttpToHttpsConfig {

    @Value("${server.http.port:8080}")
    private int httpPort;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpConnectorCustomizer() {
        return factory -> {
            Connector connector = new Connector("HTTP/1.1");
            connector.setPort(httpPort);
            connector.setSecure(false);
            connector.setScheme("http");
            factory.addAdditionalConnectors(connector);
        };
    }
}
