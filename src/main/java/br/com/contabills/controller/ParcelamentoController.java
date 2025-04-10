package br.com.contabills.controller;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.contabills.model.Parcelamento;
import br.com.contabills.service.ParcelamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por manipular as requisições relacionadas à entidade
 * Parcelamento.
 *
 * Permite operações de listagem, consulta, criação, atualização e exclusão de
 * parcelamentos.
 * 
 * @author Gerson
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("parcelamentos")
@Tag(name = "Parcelamentos", description = "Manipulação de dados dos parcelamentos cadastrados")
public class ParcelamentoController {

        /**
         * Construtor padrão da classe ParcelamentoController.
         */
        public ParcelamentoController() {
        }

        @Autowired
        private ParcelamentoService parcelamentoService;

        /**
         * Lista todos os parcelamentos com suporte à paginação.
         *
         * @param pageable parâmetros de paginação
         * @return página com lista de parcelamentos
         */
        @GetMapping
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Listar Parcelamentos", description = "Retorna todos os parcelamentos cadastrados")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Lista não encontrada")
        })
        public ResponseEntity<Page<Parcelamento>> index(
                        @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
                log.info("Listando todos os parcelamentos");

                Page<Parcelamento> parcelamentosPage = parcelamentoService.listarParcelamentos(pageable);

                return ResponseEntity.ok(parcelamentosPage);
        }

        /**
         * Busca um parcelamento pelo seu ID.
         *
         * @param id identificador do parcelamento
         * @return parcelamento encontrado
         */
        @GetMapping("/{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Detalhes do Parcelamento", description = "Retorna o parcelamento cadastrado com o id informado")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Os dados foram retornados com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Parcelamento não encontrado")
        })
        public ResponseEntity<Parcelamento> get(@PathVariable Long id) {
                log.info("Buscando parcelamento com id: {}", id);
                return ResponseEntity.ok(parcelamentoService.buscarParcelamentoPorId(id));
        }

        /**
         * Cadastra um novo parcelamento.
         *
         * @param parcelamento dados do novo parcelamento
         * @return parcelamento criado com status 201
         */
        @PostMapping
        @Operation(summary = "Cadastrar Parcelamento", description = "Cadastra um novo parcelamento")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Parcelamento criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos para o parcelamento")
        })
        public ResponseEntity<Parcelamento> create(@RequestBody Parcelamento parcelamento) {
                log.info("Cadastrando parcelamento: {}", parcelamento);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(parcelamentoService.cadastrarParcelamento(parcelamento));
        }

        /**
         * Atualiza completamente os dados de um parcelamento existente.
         *
         * @param id           identificador do parcelamento
         * @param parcelamento dados atualizados
         * @return parcelamento atualizado
         */
        @PutMapping("/{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Alterar Parcelamento", description = "Atualiza os dados de um parcelamento existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Alteração realizada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Parcelamento não encontrado"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos para alteração")
        })
        public ResponseEntity<Parcelamento> update(@PathVariable Long id, @RequestBody Parcelamento parcelamento) {
                log.info("Atualizando parcelamento com id {} para {}", id, parcelamento);
                return ResponseEntity.ok(parcelamentoService.atualizarParcelamento(id, parcelamento));
        }

        /**
         * Atualiza parcialmente os dados de um parcelamento.
         *
         * @param id      identificador do parcelamento
         * @param updates mapa com os campos a serem atualizados
         * @return parcelamento atualizado
         */
        @PatchMapping("/{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Atualizar Dados do Parcelamento", description = "Atualiza parcialmente os dados de um parcelamento")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Parcelamento atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização")
        })
        public ResponseEntity<Parcelamento> updateParcelamento(@PathVariable Long id,
                        @RequestBody Map<String, Object> updates) {
                log.info("Atualizando parcelamento id {} com dados {}", id, updates);
                return ResponseEntity.ok(parcelamentoService.atualizarDadosParcialmente(id, updates));
        }

        /**
         * Exclui um parcelamento pelo seu ID.
         *
         * @param id identificador do parcelamento
         * @return resposta com status 204
         */
        @DeleteMapping("/{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Excluir Parcelamento", description = "Exclui o parcelamento com o id informado")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Parcelamento excluído com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Parcelamento não encontrado")
        })
        public ResponseEntity<Void> destroy(@PathVariable Long id) {
                log.info("Deletando parcelamento com id: {}", id);
                parcelamentoService.excluirParcelamento(id);
                return ResponseEntity.noContent().build();
        }
}
