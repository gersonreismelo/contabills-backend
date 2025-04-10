package br.com.contabills.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuração da documentação da API utilizando OpenAPI (Swagger 3).
 *
 * Define informações básicas da API como título, versão, descrição e contato.
 * Adiciona também configuração de segurança para autenticação via JWT (Bearer
 * Token).
 *
 * @author Gerson
 * @version 1.0
 */
@Configuration
public class DocumentationConfig {

        /**
         * Construtor padrão da classe {@code DocumentationConfig}.
         */
        public DocumentationConfig() {
        }

        /**
         * Retorna a configuração personalizada para o Swagger/OpenAPI.
         *
         * @return uma instância de {@link OpenAPI} com as informações da aplicação.
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API da Contabills")
                                                .contact(new Contact()
                                                                .email("gersonreismelo@gmail.com.br")
                                                                .name("Contabills"))
                                                .version("v1")
                                                .description(
                                                                "Sistema de gestão contábil eficiente, desenvolvido para facilitar o controle financeiro e a geração de documentos para empresas."))
                                .components(new Components()
                                                .addSecuritySchemes("bearer-key",
                                                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }

}