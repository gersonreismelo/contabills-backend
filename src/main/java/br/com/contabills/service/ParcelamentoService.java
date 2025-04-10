package br.com.contabills.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.model.Empresa;
import br.com.contabills.model.Parcelamento;
import br.com.contabills.repository.EmpresaRepository;
import br.com.contabills.repository.ParcelamentoRepository;

/**
 * Serviço responsável pela manipulação das regras de negócio relacionadas à
 * entidade {@link Parcelamento}.
 * 
 * Inclui funcionalidades para listar, buscar, cadastrar, atualizar total ou
 * parcialmente
 * e excluir parcelamentos, além de associar corretamente empresas existentes.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class ParcelamentoService {

    /**
     * Construtor padrão necessário para frameworks e serialização.
     */
    public ParcelamentoService() {
    }

    @Autowired
    private ParcelamentoRepository parcelamentoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * Lista os parcelamentos em formato paginado.
     *
     * @param pageable - informações de paginação e ordenação.
     * @return {@code Page<Parcelamento>} - página contendo os parcelamentos.
     */
    public Page<Parcelamento> listarParcelamentos(Pageable pageable) {
        return parcelamentoRepository.findAll(pageable);
    }

    /**
     * Busca um parcelamento pelo seu ID.
     *
     * @param id - ID do parcelamento a ser buscado.
     * @return Parcelamento - parcelamento encontrado.
     * @throws ResponseStatusException com status 404 se o parcelamento não for
     *                                 encontrado.
     */
    public Parcelamento buscarParcelamentoPorId(Long id) {
        return parcelamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcelamento não encontrado"));
    }

    /**
     * Cadastra um novo parcelamento.
     *
     * @param parcelamento - objeto parcelamento a ser cadastrado.
     * @return Parcelamento - parcelamento cadastrado.
     */
    public Parcelamento cadastrarParcelamento(Parcelamento parcelamento) {
        return parcelamentoRepository.save(parcelamento);
    }

    /**
     * Atualiza todas as informações de um parcelamento existente.
     *
     * @param id                     - ID do parcelamento a ser atualizado.
     * @param parcelamentoAtualizado - objeto parcelamento contendo os novos dados.
     * @return Parcelamento - parcelamento atualizado.
     * @throws ResponseStatusException se o parcelamento não for encontrado.
     */
    public Parcelamento atualizarParcelamento(Long id, Parcelamento parcelamentoAtualizado) {
        Parcelamento parcelamentoExistente = buscarParcelamentoPorId(id);
        parcelamentoExistente.setRegistroDoParcelamento(parcelamentoAtualizado.getRegistroDoParcelamento());
        parcelamentoExistente.setTipoParcelamento(parcelamentoAtualizado.getTipoParcelamento());
        return parcelamentoRepository.save(parcelamentoExistente);
    }

    /**
     * Atualiza parcialmente os dados de um parcelamento.
     *
     * Campos permitidos para atualização parcial:
     * - registroDoParcelamento: (String)
     * - tipoParcelamento: (String)
     * - empresa: (Map com a chave "apelidoId", exemplo: {"apelidoId": 123})
     *
     * @param id      - ID do parcelamento a ser atualizado.
     * @param updates - Map dos campos a serem atualizados e seus respectivos novos
     *                valores.
     * @return Parcelamento - parcelamento atualizado.
     * @throws ResponseStatusException se algum campo inválido for informado ou se a
     *                                 empresa não for encontrada.
     */
    public Parcelamento atualizarDadosParcialmente(Long id, Map<String, Object> updates) {
        Parcelamento parcelamento = buscarParcelamentoPorId(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "registroDoParcelamento":
                    parcelamento.setRegistroDoParcelamento((String) value);
                    break;
                case "tipoParcelamento":
                    parcelamento.setTipoParcelamento((String) value);
                    break;
                case "empresa":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> empresaData = (Map<String, Object>) value;
                    Long empresaId = (Long) empresaData.get("apelidoId");
                    Empresa empresa = empresaRepository.findById(empresaId)
                            .orElseThrow(
                                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
                    parcelamento.setEmpresa(empresa);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo inválido: " + key);
            }
        });

        return parcelamentoRepository.save(parcelamento);
    }

    /**
     * Exclui um parcelamento pelo seu ID.
     *
     * @param id - ID do parcelamento a ser excluído.
     * @throws ResponseStatusException se o parcelamento não for encontrado.
     */
    public void excluirParcelamento(Long id) {
        Parcelamento parcelamento = buscarParcelamentoPorId(id);
        parcelamentoRepository.delete(parcelamento);
    }
}
