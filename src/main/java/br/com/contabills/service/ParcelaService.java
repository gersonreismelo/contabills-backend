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

@Service
public class ParcelaService {

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ParcelamentoRepository parcelamentoRepository;

    /**
     * Lista todas as parcelas paginadas.
     * 
     * @param pageable - informações de paginação e ordenação
     * @return Page<Parcela> - página de parcelas
     */
    public Page<Parcela> listarParcelas(Pageable pageable) {
        return parcelaRepository.findAll(pageable);
    }

    /**
     * Busca uma parcela pelo seu ID.
     * 
     * @param id - ID da parcela
     * @return Parcela - parcela encontrada
     * @throws ResponseStatusException caso a parcela não seja encontrada
     */
    public Parcela buscarParcelaPorId(Long id) {
        return parcelaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcela não encontrada"));
    }

    /**
     * Cadastra uma nova parcela.
     * 
     * @param parcela - objeto parcela a ser cadastrado
     * @return Parcela - parcela cadastrada
     */
    public Parcela cadastrarParcela(Parcela parcela) {
        return parcelaRepository.save(parcela);
    }

    /**
     * Atualiza uma parcela existente.
     * 
     * @param id - ID da parcela a ser atualizada
     * @param parcelaAtualizada - objeto parcela com os novos dados
     * @return Parcela - parcela atualizada
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
     * - numero: (Integer)
     * - valor: (Double)
     * - enviadoMesAtual: (Boolean)
     * - parcelamento: (Map com o id, ex.: {"id": 123}) – opcional
     * 
     * @param id - ID da parcela
     * @param updates - Map dos campos a serem atualizados e seus respectivos novos valores
     * @return Parcela - parcela atualizada
     */
    public Parcela atualizarDadosParcialmente(Long id, Map<String, Object> updates) {
        Parcela parcela = buscarParcelaPorId(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "numero":
                    // Converte para Integer - certifique-se que o valor enviado seja compatível
                    parcela.setNumero((Integer) value);
                    break;
                case "valor":
                    // Converte para Double. Use Double.valueOf se o objeto estiver como String ou Number.
                    parcela.setValor(Double.valueOf(value.toString()));
                    break;
                case "enviadoMesAtual":
                    parcela.setEnviadoMesAtual((Boolean) value);
                    break;
                case "parcelamento":
                    // Se desejar atualizar o relacionamento com Parcelamento, espera-se receber um Map que contenha o campo "id".
                    @SuppressWarnings("unchecked")
                    Map<String, Object> parcelamentoData = (Map<String, Object>) value;
                    Long parcelamentoId = Long.valueOf(parcelamentoData.get("id").toString());
                    Parcelamento parcelamento = parcelamentoRepository.findById(parcelamentoId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcelamento não encontrado"));
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
     * @param id - ID da parcela a ser excluída
     */
    public void excluirParcela(Long id) {
        Parcela parcela = buscarParcelaPorId(id);
        parcelaRepository.delete(parcela);
    }
}
