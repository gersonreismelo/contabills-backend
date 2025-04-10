package br.com.contabills.controller;

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

/**
 * Controller responsável por manipular as requisições relacionadas à entidade
 * Empresa.
 * 
 * Permite operações de criação, leitura, atualização e exclusão (CRUD) com
 * suporte a paginação e autenticação JWT.
 * 
 * @author Gerson
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("empresas")
@Tag(name = "Empresa", description = "Manipulação de dados das empresas")
public class EmpresaController {

    /**
     * Construtor padrão necessário para o Spring Framework realizar a injeção de
     * dependência.
     */
    public EmpresaController() {
    }

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpresaService empresaService;

    /**
     * Lista todas as empresas com suporte à paginação.
     *
     * @param pageable parâmetros de paginação
     * @return página de empresas
     */
    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Listar empresas", description = "Retorna todas as empresas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<Page<Empresa>> index(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        log.info("Listando todas as empresas com paginação");
        Page<Empresa> empresasPage = empresaService.listarEmpresas(pageable);
        return ResponseEntity.ok(empresasPage);
    }

    /**
     * Busca uma empresa pelo seu ID.
     *
     * @param id identificador da empresa
     * @return empresa encontrada ou 404 se não existir
     */
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

    /**
     * Cadastra uma nova empresa.
     *
     * @param empresa objeto com os dados da nova empresa
     * @return empresa criada com status 201
     */
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

    /**
     * Atualiza os dados de uma empresa existente.
     *
     * @param id      identificador da empresa
     * @param empresa objeto com os dados atualizados
     * @return empresa atualizada ou 404 se não encontrada
     */
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

    /**
     * Exclui uma empresa existente.
     *
     * @param id identificador da empresa
     * @return resposta com status 204 se sucesso
     */
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
