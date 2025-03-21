package br.com.contabills.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Socio;

public interface SocioRepository extends JpaRepository<Socio, Long> {
    Optional<Socio> findByCpf(String cpf); // Método para buscar sócio pelo CPF

    boolean existsByCpf(String cpf);

    Page<Socio> findByNomeContaining(String nome, Pageable pageable);
}
