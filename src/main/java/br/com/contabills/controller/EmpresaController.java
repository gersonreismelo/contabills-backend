package br.com.contabills.controller;

import java.util.HashMap;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.contabills.model.Empresa;
import br.com.contabills.repository.EmpresaRepository;
import br.com.contabills.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("empresas")
@Tag(name = "Empresa", description = "Manipulação de dados das empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Listar empresas", description = "Retorna todas as empresas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<Map<String, Object>> index(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        log.info("Listando todas as empresas com paginação");
        Page<Empresa> empresasPage = empresaRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("results", empresasPage.getContent());
        response.put("info", Map.of(
                "count", empresasPage.getTotalElements()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes empresa", description = "Retorna a empresa cadastrada com o id informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Empresa> get(@PathVariable Long id) {
        log.info("Buscar empresa por id: {}", id);

        return empresaRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastrar empresa", description = "Cadastra uma nova empresa com os dados informados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empresa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro"),
            @ApiResponse(responseCode = "409", description = "Já existe uma empresa com esse dado")
    })
    public ResponseEntity<Empresa> create(@RequestBody Empresa empresa) {
        log.info("Cadastrando empresa: {}", empresa);
        Empresa novaEmpresa = empresaService.cadastrarEmpresa(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEmpresa);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa", description = "Atualiza os dados da empresa com o id informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Empresa> update(@PathVariable Long id, @RequestBody Empresa empresa) {
        log.info("Atualizando empresa id {} para {}", id, empresa);
        Empresa empresaAtualizada = empresaService.atualizarEmpresa(id, empresa);
        return ResponseEntity.ok(empresaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir empresa", description = "Exclui a empresa com o id informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empresa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        log.info("Apagando empresa {}", id);
        empresaService.excluirEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}
