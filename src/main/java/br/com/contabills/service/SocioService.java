package br.com.contabills.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.model.Empresa;
import br.com.contabills.model.Socio;
import br.com.contabills.repository.EmpresaRepository;
import br.com.contabills.repository.SocioRepository;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public Page<Socio> listarSocios(String busca, Pageable pageable) {
        return (busca == null) ? socioRepository.findAll(pageable)
                : socioRepository.findByNomeContaining(busca, pageable);
    }

    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sócio não encontrado"));
    }

    public Socio cadastrar(Socio socio) {
        return socioRepository.save(socio);
    }

    public Socio atualizar(Long id, Socio socio) {
        buscarPorId(id);
        socio.setId(id);
        return socioRepository.save(socio);
    }

    public void deletar(Long id) {
        Socio socio = buscarPorId(id);
        List<Empresa> empresas = socio.getEmpresas();
        for (Empresa empresa : empresas) {
            empresa.getSocios().remove(socio);
            empresaRepository.save(empresa);
        }
        socioRepository.deleteById(id);
    }
}
