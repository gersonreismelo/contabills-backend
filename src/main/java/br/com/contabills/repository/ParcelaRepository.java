package br.com.contabills.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.contabills.model.Parcela;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
}
