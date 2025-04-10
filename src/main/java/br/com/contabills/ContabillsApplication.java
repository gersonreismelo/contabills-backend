package br.com.contabills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Contabills.
 * 
 * Esta classe inicia a aplicação Spring Boot.
 * 
 * @author Gerson
 * @version 1.0
 */
@SpringBootApplication
public class ContabillsApplication {

    /**
     * Construtor padrão da aplicação.
     */
    public ContabillsApplication() {
    }

    /**
     * Método principal da aplicação. Responsável por iniciar o Spring Boot.
     * 
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(ContabillsApplication.class, args);
    }
}
