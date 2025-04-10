package br.com.contabills.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Parcelamento;

/**
 * Interface de repositório para a entidade {@link Parcelamento}.
 * 
 * Fornece métodos para operações básicas de persistência no banco de dados.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
public interface ParcelamentoRepository extends JpaRepository<Parcelamento, Long> {

}
