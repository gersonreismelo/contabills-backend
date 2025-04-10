package br.com.contabills.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.contabills.repository.UsuarioRepository;

/**
 * Serviço responsável por autenticar usuários com base em seu e-mail.
 * 
 * Implementa {@link UserDetailsService}, interface do Spring Security utilizada para
 * recuperar os dados do usuário durante o processo de autenticação.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    /**
     * Construtor padrão da classe AuthenticationService.
     */
    public AuthenticationService() {
        // Construtor padrão
    }

    /**
     * Carrega os detalhes do usuário com base no email fornecido (username).
     *
     * @param username Email do usuário que está tentando se autenticar.
     * @return UserDetails Objeto contendo as informações do usuário (como username, senha, 
     *         e autoridades) que serão utilizadas pelo Spring Security.
     * @throws UsernameNotFoundException se o usuário não for encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
