package br.com.contabills.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Parcelamento;

public interface ParcelamentoRepository extends JpaRepository<Parcelamento, Long> {

}
