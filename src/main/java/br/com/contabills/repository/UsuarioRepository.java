package br.com.contabills.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Usuario;

/**
 * Interface de repositório para a entidade {@link Usuario}.
 * 
 * Fornece métodos de acesso a dados para operações com usuários,
 * incluindo busca por nome e por e-mail.
 *
 * 
 * @author Gerson
 * @version 1.0
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuários cujo nome contenha o valor informado, com suporte à paginação.
     * 
     * @param busca    termo a ser buscado no nome do usuário
     * @param pageable informações de paginação
     * @return página de usuários que correspondem ao filtro
     */
    Page<Usuario> findByNomeContaining(String busca, Pageable pageable);

    /**
     * Busca um usuário pelo e-mail.
     * 
     * @param email e-mail do usuário
     * @return Optional contendo o usuário, se encontrado
     */
    Optional<Usuario> findByEmail(String email);

}