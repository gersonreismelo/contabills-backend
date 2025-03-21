package br.com.contabills.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Page<Empresa> findByRazaoSocialContaining(String nome, Pageable pageable);

}
