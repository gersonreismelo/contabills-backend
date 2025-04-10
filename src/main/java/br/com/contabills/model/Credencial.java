package br.com.contabills.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Representa as credenciais de um usuário no sistema, contendo um email e uma senha.
 * 
 * Esta classe fornece um método para converter as credenciais em um objeto
 * {@link Authentication} do Spring Security, que pode ser utilizado no processo
 * de autenticação.
 * 
 * @author Gerson
 * @version 1.0
 * @param email o e-mail do usuário
 * @param senha a senha do usuário
 */
public record Credencial(String email, String senha) {

    /**
     * Converte esta instância de {@code Credencial} em um objeto de autenticação.
     *
     * O método cria uma instância de {@link UsernamePasswordAuthenticationToken} utilizando
     * o email e a senha contidos nesta credencial.
     *
     * @return um {@link Authentication} contendo o email e a senha.
     */
    public Authentication toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, senha);
    }
}
