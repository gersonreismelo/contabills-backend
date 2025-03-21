package br.com.contabills.service;

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
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SocioRepository socioRepository;

    public Page<Empresa> listarEmpresas(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    public Empresa buscarEmpresaPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }

    public Empresa cadastrarEmpresa(Empresa empresa) {
        tratarSocios(empresa);
        return empresaRepository.save(empresa);
    }

    public Empresa atualizarEmpresa(Long id, Empresa empresa) {
        buscarEmpresaPorId(id);
        tratarSocios(empresa);
        return empresaRepository.save(empresa);
    }

    public void excluirEmpresa(Long id) {
        Empresa empresa = buscarEmpresaPorId(id);
        for (Socio socio : empresa.getSocios()) {
            socio.getEmpresas().remove(empresa);
        }
        empresaRepository.deleteById(id);
    }

    private void tratarSocios(Empresa empresa) {
        for (Socio socio : empresa.getSocios()) {
            if (socio.getCpf() != null) {
                Socio socioExistente = socioRepository.findByCpf(socio.getCpf())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "CPF " + socio.getCpf() + " não pertence a nenhum sócio"));
                socio.setId(socioExistente.getId());
            }
        }
    }
}
