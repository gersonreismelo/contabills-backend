package br.com.contabills.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.contabills.model.Empresa;

/**
 * Interface de repositório para a entidade {@link Empresa}.
 * 
 * Fornece métodos para operações de persistência e consultas personalizadas.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Busca empresas cuja razão social contenha a string fornecida.
     * 
     * @param nome     parte do nome (razão social) a ser buscado
     * @param pageable objeto para paginação dos resultados
     * @return página contendo as empresas encontradas
     */
    Page<Empresa> findByRazaoSocialContaining(String nome, Pageable pageable);

}
