package br.com.contabills.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.model.Parcela;
import br.com.contabills.model.Parcelamento;
import br.com.contabills.repository.ParcelaRepository;
import br.com.contabills.repository.ParcelamentoRepository;

/**
 * Serviço responsável pelas regras de negócio relacionadas à entidade {@link Parcela}.
 * 
 * Inclui operações para listagem, consulta, criação, atualização completa ou parcial
 * e exclusão de parcelas, além do gerenciamento de suas associações com parcelamentos.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class ParcelaService {

    /**
     * Construtor padrão necessário para frameworks e serialização.
     */
    public ParcelaService() {
    }

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ParcelamentoRepository parcelamentoRepository;

    /**
     * Lista todas as parcelas paginadas.
     * 
     * @param pageable informações de paginação e ordenação
     * @return página de {@link Parcela}
     */
    public Page<Parcela> listarParcelas(Pageable pageable) {
        return parcelaRepository.findAll(pageable);
    }

    /**
     * Busca uma parcela pelo seu ID.
     * 
     * @param id ID da parcela
     * @return a {@link Parcela} encontrada
     * @throws ResponseStatusException caso a parcela não seja encontrada
     */
    public Parcela buscarParcelaPorId(Long id) {
        return parcelaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcela não encontrada"));
    }

    /**
     * Cadastra uma nova parcela.
     * 
     * @param parcela objeto {@link Parcela} a ser cadastrado
     * @return a {@link Parcela} cadastrada
     */
    public Parcela cadastrarParcela(Parcela parcela) {
        return parcelaRepository.save(parcela);
    }

    /**
     * Atualiza uma parcela existente.
     * 
     * @param id ID da parcela a ser atualizada
     * @param parcelaAtualizada objeto {@link Parcela} com os novos dados
     * @return a {@link Parcela} atualizada
     */
    public Parcela atualizarParcela(Long id, Parcela parcelaAtualizada) {
        Parcela parcelaExistente = buscarParcelaPorId(id);

        parcelaExistente.setNumero(parcelaAtualizada.getNumero());
        parcelaExistente.setValor(parcelaAtualizada.getValor());
        parcelaExistente.setEnviadoMesAtual(parcelaAtualizada.isEnviadoMesAtual());

        return parcelaRepository.save(parcelaExistente);
    }

    /**
     * Atualiza parcialmente os dados de uma parcela.
     * 
     * Campos permitidos para atualização parcial:
     * <ul>
     * <li>numero: (Integer)</li>
     * <li>valor: (Double)</li>
     * <li>enviadoMesAtual: (Boolean)</li>
     * <li>parcelamento: (Map com o id, ex.: {"id": 123}) – opcional</li>
     * </ul>
     * 
     * @param id ID da parcela
     * @param updates mapa dos campos a serem atualizados e seus respectivos novos valores
     * @return a {@link Parcela} atualizada
     * @throws ResponseStatusException caso algum campo seja inválido ou o parcelamento não exista
     */
    public Parcela atualizarDadosParcialmente(Long id, Map<String, Object> updates) {
        Parcela parcela = buscarParcelaPorId(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "numero":
                    parcela.setNumero((Integer) value);
                    break;
                case "valor":
                    parcela.setValor(Double.valueOf(value.toString()));
                    break;
                case "enviadoMesAtual":
                    parcela.setEnviadoMesAtual((Boolean) value);
                    break;
                case "parcelamento":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> parcelamentoData = (Map<String, Object>) value;
                    Long parcelamentoId = Long.valueOf(parcelamentoData.get("id").toString());
                    Parcelamento parcelamento = parcelamentoRepository.findById(parcelamentoId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Parcelamento não encontrado"));
                    parcela.setParcelamento(parcelamento);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo inválido: " + key);
            }
        });

        return parcelaRepository.save(parcela);
    }

    /**
     * Exclui uma parcela pelo seu ID.
     * 
     * @param id ID da parcela a ser excluída
     */
    public void excluirParcela(Long id) {
        Parcela parcela = buscarParcelaPorId(id);
        parcelaRepository.delete(parcela);
    }
}
