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

@Service
public class ParcelamentoService {

    @Autowired
    private ParcelamentoRepository parcelamentoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public Page<Parcelamento> listarParcelamentos(Pageable pageable) {
        return parcelamentoRepository.findAll(pageable);
    }

    public Parcelamento buscarParcelamentoPorId(Long id) {
        return parcelamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcelamento não encontrado"));
    }

    public Parcelamento cadastrarParcelamento(Parcelamento parcelamento) {
        return parcelamentoRepository.save(parcelamento);
    }

    public Parcelamento atualizarParcelamento(Long id, Parcelamento parcelamentoAtualizado) {
        Parcelamento parcelamentoExistente = buscarParcelamentoPorId(id);
        parcelamentoExistente.setEnviadoMesAtual(parcelamentoAtualizado.isEnviadoMesAtual());
        parcelamentoExistente.setNumeroParcelamento(parcelamentoAtualizado.getNumeroParcelamento());
        parcelamentoExistente.setValorParcela(parcelamentoAtualizado.getValorParcela());
        parcelamentoExistente.setTipoParcelamento(parcelamentoAtualizado.getTipoParcelamento()); // Adicionado

        return parcelamentoRepository.save(parcelamentoExistente);
    }

    public Parcelamento atualizarDadosParcialmente(Long id, Map<String, Object> updates) {
        Parcelamento parcelamento = buscarParcelamentoPorId(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "numeroParcelamento":
                    parcelamento.setNumeroParcelamento((Long) value);
                    break;
                case "enviadoMesAtual":
                    parcelamento.setEnviadoMesAtual((Boolean) value);
                    break;
                case "valorParcela":
                    parcelamento.setValorParcela((Double) value);
                    break;
                case "empresa":
                    @SuppressWarnings("unchecked")
                    Long empresaId = (Long) ((Map<String, Object>) value).get("apelidoId");
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

    public void excluirParcelamento(Long id) {
        Parcelamento parcelamento = buscarParcelamentoPorId(id);
        parcelamentoRepository.delete(parcelamento);
    }
}
