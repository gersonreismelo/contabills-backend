package br.com.contabills.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Parcela;

/**
 * Interface de repositório para a entidade {@link Parcela}.
 * 
 * Responsável por fornecer métodos de acesso a dados para as parcelas,
 * incluindo operações padrão de CRUD.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
}
