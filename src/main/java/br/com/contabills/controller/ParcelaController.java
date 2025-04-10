package br.com.contabills.controller;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.contabills.model.Parcela;
import br.com.contabills.service.EmailService;
import br.com.contabills.service.ParcelaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador responsável pelas operações relacionadas às parcelas.
 * 
 * Permite operações de listagem, detalhamento, criação, atualização, exclusão
 * e envio de PDF por e-mail.
 * 
 * @author Gerson
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("parcelas")
@Tag(name = "Parcelas", description = "Manipulação de dados das parcelas cadastradas")
public class ParcelaController {

    /**
     * Construtor padrão da classe ParcelaController.
     */
    public ParcelaController() {
    }

    @Autowired
    private ParcelaService parcelaService;

    @Autowired
    private EmailService emailService;

    /**
     * Lista todas as parcelas com suporte à paginação.
     *
     * @param pageable parâmetros de paginação
     * @return página contendo as parcelas
     */
    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Listar Parcelas", description = "Retorna todas as parcelas cadastradas com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<Page<Parcela>> index(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        log.info("Listando todas as parcelas paginadas");
        Page<Parcela> parcelasPage = parcelaService.listarParcelas(pageable);
        return ResponseEntity.ok(parcelasPage);
    }

    /**
     * Retorna os detalhes de uma parcela pelo ID.
     *
     * @param id identificador da parcela
     * @return parcela encontrada ou erro 404
     */
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Detalhar Parcela", description = "Retorna a parcela com o id informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parcela encontrada"),
            @ApiResponse(responseCode = "404", description = "Parcela não encontrada")
    })
    public ResponseEntity<Parcela> get(@PathVariable Long id) {
        log.info("Buscando parcela com id: {}", id);
        return ResponseEntity.ok(parcelaService.buscarParcelaPorId(id));
    }

    /**
     * Cadastra uma nova parcela.
     *
     * @param parcela dados da nova parcela
     * @return parcela criada com status 201
     */
    @PostMapping
    @Operation(summary = "Cadastrar Parcela", description = "Cadastra uma nova parcela")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parcela criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a parcela")
    })
    public ResponseEntity<Parcela> create(@RequestBody Parcela parcela) {
        log.info("Cadastrando parcela: {}", parcela);
        return ResponseEntity.status(HttpStatus.CREATED).body(parcelaService.cadastrarParcela(parcela));
    }

    /**
     * Envia um PDF anexado da parcela por e-mail e atualiza o status da parcela.
     *
     * @param idParcela identificador da parcela
     * @param file      arquivo PDF
     * @param subject   assunto do e-mail
     * @param text      corpo do e-mail
     * @return mensagem de sucesso ou erro
     */
    @PostMapping("/enviar-pdf/{idParcela}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Enviar PDF e marcar parcela como enviada", description = "Envia o PDF da parcela para o e-mail da empresa associada ao parcelamento e, se o envio for bem-sucedido, atualiza o status da parcela para enviada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E-mail enviado com sucesso e status da parcela atualizado"),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar e-mail ou atualizar a parcela")
    })
    public ResponseEntity<String> enviarPdfParaEmpresa(
            @PathVariable Long idParcela,
            @RequestParam("file") MultipartFile file,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text) {

        log.info("Enviando e-mail com anexo PDF para parcela ID: {}", idParcela);

        try {
            Parcela parcela = parcelaService.buscarParcelaPorId(idParcela);

            String email = parcela.getParcelamento().getEmpresa().getEmail();

            emailService.sendEmailWithAttachment(email, subject, text, file);

            parcela.setEnviadoMesAtual(true);
            parcelaService.atualizarParcela(idParcela, parcela);

            return ResponseEntity.ok("E-mail enviado com sucesso e parcela marcada como enviada!");
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar e-mail: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Atualiza completamente os dados de uma parcela existente.
     *
     * @param id      identificador da parcela
     * @param parcela nova versão da parcela
     * @return parcela atualizada
     */
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualizar Parcela", description = "Atualiza os dados da parcela existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parcela atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Parcela não encontrada")
    })
    public ResponseEntity<Parcela> update(@PathVariable Long id, @RequestBody Parcela parcela) {
        log.info("Atualizando parcela com id {} para {}", id, parcela);
        return ResponseEntity.ok(parcelaService.atualizarParcela(id, parcela));
    }

    /**
     * Atualiza parcialmente os dados de uma parcela.
     *
     * @param id      identificador da parcela
     * @param updates mapa contendo os campos a serem atualizados
     * @return parcela atualizada parcialmente
     */
    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualizar Dados da Parcela", description = "Atualiza parcialmente os dados de uma parcela")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parcela atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização")
    })
    public ResponseEntity<Parcela> updateParcel(@PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        log.info("Atualizando dados da parcela de id {} com: {}", id, updates);
        return ResponseEntity.ok(parcelaService.atualizarDadosParcialmente(id, updates));
    }

    /**
     * Exclui uma parcela existente.
     *
     * @param id identificador da parcela
     * @return status 204 se sucesso
     */
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Excluir Parcela", description = "Exclui a parcela com o id informado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parcela excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Parcela não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Excluindo parcela com id: {}", id);
        parcelaService.excluirParcela(id);
        return ResponseEntity.noContent().build();
    }
}
