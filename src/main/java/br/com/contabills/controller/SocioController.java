package br.com.contabills.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.contabills.model.Socio;
import br.com.contabills.service.SocioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável pelo gerenciamento dos dados de sócios.
 * 
 * Permite operações de listagem, consulta, cadastro, atualização e exclusão de sócios.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("socios")
@Tag(name = "Sócios", description = "Manipulação de dados dos sócios cadastrados")
public class SocioController {

    /**
     * Construtor padrão
     */
    public SocioController() {
    }

    @Autowired
    private SocioService socioService;

    /**
     * Lista todos os sócios com suporte a busca por nome/termo e paginação.
     *
     * @param busca termo de busca opcional
     * @param pageable parâmetros de paginação
     * @return página com os sócios encontrados
     */
    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Listar sócios", description = "Retorna todos os sócios cadastrados")
    @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso")
    public Page<Socio> index(@RequestParam(required = false) String busca,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        log.info("Buscar Sócios");
        return socioService.listarSocios(busca, pageable);
    }

    /**
     * Retorna os dados de um sócio específico.
     *
     * @param id identificador do sócio
     * @return sócio encontrado
     */
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Detalhes do sócio", description = "Retorna o sócio cadastrado com o id informado")
    @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso")
    public Socio show(@PathVariable Long id) {
        log.info("Buscar Sócio " + id);
        return socioService.buscarPorId(id);
    }

    /**
     * Cadastra um novo sócio.
     *
     * @param socio dados do novo sócio
     * @return sócio criado
     */
    @PostMapping
    @Operation(summary = "Cadastrar sócio", description = "Cadastra um novo sócio")
    @ApiResponse(responseCode = "201", description = "Sócio criado com sucesso")
    public ResponseEntity<Socio> create(@RequestBody Socio socio) {
        log.info("Cadastrando Sócio: " + socio);
        Socio novoSocio = socioService.cadastrar(socio);
        return ResponseEntity.ok(novoSocio);
    }

    /**
     * Atualiza os dados de um sócio existente.
     *
     * @param id identificador do sócio
     * @param socio dados atualizados
     * @return sócio atualizado
     */
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualizar sócio", description = "Atualiza os dados do sócio com o id informado")
    @ApiResponse(responseCode = "200", description = "Alteração realizada com sucesso")
    public Socio update(@PathVariable Long id, @RequestBody Socio socio) {
        log.info("Atualizando Sócio " + id);
        return socioService.atualizar(id, socio);
    }

    /**
     * Exclui um sócio pelo seu ID.
     *
     * @param id identificador do sócio
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Excluir sócio", description = "Exclui o sócio com o id informado")
    @ApiResponse(responseCode = "204", description = "Exclusão realizada com sucesso")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando Sócio " + id);
        socioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca um sócio pelo seu CPF.
     *
     * @param cpf CPF do sócio
     * @return sócio encontrado
     */
    @GetMapping("/buscarPorCpf/{cpf}") 
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Buscar sócio por CPF", description = "Retorna o sócio cadastrado com o CPF informado")
    @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso")
    public Socio buscarPorCpf(@PathVariable String cpf) {
        log.info("Buscar Sócio por CPF: " + cpf);
        return socioService.buscarPorCpf(cpf);
    }
}