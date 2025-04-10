package br.com.contabills.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Socio;

/**
 * Interface de repositório para a entidade {@link Socio}.
 * 
 * Fornece métodos para acessar os dados dos sócios, incluindo
 * buscas por CPF e por nome, além de verificação de existência.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
public interface SocioRepository extends JpaRepository<Socio, Long> {

    /**
     * Busca um sócio pelo CPF.
     * 
     * @param cpf CPF do sócio
     * @return Optional contendo o sócio, se encontrado
     */
    Optional<Socio> findByCpf(String cpf);

    /**
     * Verifica se já existe um sócio cadastrado com o CPF informado.
     * 
     * @param cpf CPF do sócio
     * @return true se existir, false caso contrário
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca sócios com nome contendo o valor informado, paginando os resultados.
     * 
     * @param nome     parte do nome a ser buscada
     * @param pageable informações de paginação
     * @return página de sócios que correspondem à busca
     */
    Page<Socio> findByNomeContaining(String nome, Pageable pageable);
}
