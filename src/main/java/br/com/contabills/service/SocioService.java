package br.com.contabills.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.model.Empresa;
import br.com.contabills.model.EmpresaSocio;
import br.com.contabills.model.Socio;
import br.com.contabills.repository.EmpresaRepository;
import br.com.contabills.repository.SocioRepository;

/**
 * Serviço responsável pelas regras de negócio relacionadas à entidade
 * {@link Socio}.
 * 
 * Oferece operações para listagem, consulta, cadastro, atualização e remoção de
 * sócios,
 * incluindo busca por nome e CPF, além de cuidar da integridade nas associações
 * com empresas.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class SocioService {

    /**
     * Construtor padrão
     */
    public SocioService() {
    }

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * Lista os sócios paginados, com opção de busca por nome.
     *
     * @param busca    - termo de busca para filtrar os sócios pelo nome. Se nulo,
     *                 retorna todos.
     * @param pageable - informações de paginação e ordenação.
     * @return {@code Page<Socio>} - página contendo os sócios.
     */
    public Page<Socio> listarSocios(String busca, Pageable pageable) {
        return (busca == null) ? socioRepository.findAll(pageable)
                : socioRepository.findByNomeContaining(busca, pageable);
    }

    /**
     * Busca um sócio pelo seu ID.
     *
     * @param id - ID do sócio.
     * @return Socio - sócio encontrado.
     * @throws ResponseStatusException caso o sócio não seja encontrado.
     */
    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sócio não encontrado"));
    }

    /**
     * Cadastra um novo sócio.
     *
     * @param socio - objeto sócio a ser cadastrado.
     * @return Socio - sócio cadastrado.
     */
    public Socio cadastrar(Socio socio) {
        return socioRepository.save(socio);
    }

    /**
     * Atualiza as informações de um sócio existente.
     *
     * @param id    - ID do sócio a ser atualizado.
     * @param socio - objeto sócio com os novos dados.
     * @return Socio - sócio atualizado.
     * @throws ResponseStatusException caso o sócio não seja encontrado.
     */
    public Socio atualizar(Long id, Socio socio) {
        buscarPorId(id);
        socio.setId(id);
        return socioRepository.save(socio);
    }

    /**
     * Exclui um sócio pelo seu ID.
     * 
     * Antes de excluir, remove as associações do sócio com as empresas para evitar
     * inconsistências.
     *
     * @param id - ID do sócio a ser removido.
     * @throws ResponseStatusException caso o sócio não seja encontrado.
     */
    public void deletar(Long id) {
        Socio socio = buscarPorId(id);
        if (socio.getEmpresaSocios() != null) {
            for (EmpresaSocio es : socio.getEmpresaSocios()) {
                Empresa empresa = es.getEmpresa();
                if (empresa != null && empresa.getEmpresaSocios() != null) {
                    empresa.getEmpresaSocios().remove(es);
                    empresaRepository.save(empresa);
                }
            }
        }
        socioRepository.deleteById(id);
    }

    /**
     * Busca um sócio pelo seu CPF.
     *
     * @param cpf - CPF do sócio.
     * @return Socio - sócio encontrado.
     * @throws ResponseStatusException caso o sócio não seja encontrado.
     */
    public Socio buscarPorCpf(String cpf) {
        return socioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sócio não encontrado"));
    }
}
