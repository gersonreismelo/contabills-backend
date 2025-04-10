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
 * Serviço responsável pela manipulação das regras de negócio relacionadas à
 * entidade {@link Empresa}.
 * 
 * Inclui funcionalidades para listar, buscar, cadastrar, atualizar e excluir
 * empresas,
 * além de tratar a associação com sócios cadastrados.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class EmpresaService {

    /**
     * Construtor padrão da classe EmpresaService.
     */
    public EmpresaService() {
    }

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SocioRepository socioRepository;

    /**
     * Retorna uma página de empresas com base nas informações de paginação.
     *
     * @param pageable informações de paginação e ordenação
     * @return uma página contendo as empresas
     */
    public Page<Empresa> listarEmpresas(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    /**
     * Busca uma empresa pelo seu ID.
     *
     * @param id - ID da empresa a ser buscada.
     * @return Empresa - empresa encontrada.
     * @throws ResponseStatusException com status 404 caso a empresa não seja
     *                                 encontrada.
     */
    public Empresa buscarEmpresaPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }

    /**
     * Cadastra uma nova empresa.
     * 
     * Antes de persistir, o método trata (sincroniza) as associações com sócios
     * verificando se cada sócio informado existe com base no CPF.
     *
     * @param empresa - objeto Empresa a ser cadastrado.
     * @return Empresa - empresa cadastrada.
     * @throws ResponseStatusException se algum sócio informado não for encontrado.
     */
    public Empresa cadastrarEmpresa(Empresa empresa) {
        tratarSocios(empresa);
        return empresaRepository.save(empresa);
    }

    /**
     * Atualiza os dados de uma empresa existente.
     * 
     * Verifica a existência da empresa pelo ID, trata as associações com sócios e
     * salva os novos dados.
     *
     * @param id      - ID da empresa a ser atualizada.
     * @param empresa - objeto Empresa com os novos dados.
     * @return Empresa - empresa atualizada.
     * @throws ResponseStatusException se a empresa ou algum sócio não for
     *                                 encontrado.
     */
    public Empresa atualizarEmpresa(Long id, Empresa empresa) {
        buscarEmpresaPorId(id);
        tratarSocios(empresa);
        return empresaRepository.save(empresa);
    }

    /**
     * Exclui uma empresa pelo seu ID.
     * 
     * Antes da exclusão, remove as associações com sócios para evitar que os
     * relacionamentos fiquem inconsistentes.
     *
     * @param id - ID da empresa a ser excluída.
     * @throws ResponseStatusException se a empresa não for encontrada.
     */
    public void excluirEmpresa(Long id) {
        Empresa empresa = buscarEmpresaPorId(id);
        if (empresa.getEmpresaSocios() != null) {
            for (EmpresaSocio es : empresa.getEmpresaSocios()) {
                Socio socio = es.getSocio();
                if (socio != null && socio.getEmpresaSocios() != null) {
                    socio.getEmpresaSocios().remove(es);
                }
            }
        }
        empresaRepository.deleteById(id);
    }

    /**
     * Trata as associações entre Empresa e Sócio.
     * 
     * Para cada associação existente na empresa, verifica se o sócio informado
     * possui
     * um CPF e busca o sócio correspondente no repositório. Caso o sócio seja
     * encontrado, atualiza o ID do sócio na associação para sincronizar os dados.
     *
     * @param empresa - objeto Empresa cujas associações com sócios serão tratadas.
     * @throws ResponseStatusException se o CPF de algum sócio não for encontrado.
     */
    private void tratarSocios(Empresa empresa) {
        if (empresa.getEmpresaSocios() != null) {
            for (EmpresaSocio es : empresa.getEmpresaSocios()) {
                Socio socio = es.getSocio();
                if (socio != null && socio.getCpf() != null) {
                    Socio socioExistente = socioRepository.findByCpf(socio.getCpf())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "CPF " + socio.getCpf() + " não pertence a nenhum sócio"));
                    socio.setId(socioExistente.getId());
                    es.setSocio(socio);
                }
            }
        }
    }
}
