package br.com.contabills.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuração para habilitar o CORS (Cross-Origin Resource Sharing).
 * 
 * Permite que a aplicação Angular (frontend) localizada em http://localhost:4200 
 * faça requisições para esta API.
 * 
 * Define os métodos e headers permitidos para comunicação entre domínios diferentes.
 * 
 * @author Gerson
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Construtor padrão
     */
    public WebConfig() {
    }

    /**
     * Configura as permissões de CORS para todos os endpoints da aplicação.
     *
     * @param registry objeto {@link CorsRegistry} usado para registrar configurações de CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }
}
