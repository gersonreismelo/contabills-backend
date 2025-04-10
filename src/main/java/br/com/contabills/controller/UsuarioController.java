package br.com.contabills.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.contabills.model.Credencial;
import br.com.contabills.model.Token;
import br.com.contabills.model.Usuario;
import br.com.contabills.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável pelas operações relacionadas ao usuário.
 * 
 * Permite o cadastro, consulta, autenticação, atualização (inclusive da foto) e
 * exclusão de usuários.
 * 
 * 
 * @author Gerson
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("usuarios")
@Tag(name = "Usuário", description = "Manipulação de dados dos usuários cadastrados")
public class UsuarioController {

        /**
         * Construtor padrão
         */
        public UsuarioController() {
        }

        @Autowired
        private UsuarioService usuarioService;

        @Autowired
        private PagedResourcesAssembler<Object> assembler;

        /**
         * Lista os usuários cadastrados com paginação e suporte a busca por termo.
         *
         * @param busca    termo de busca (opcional)
         * @param pageable parâmetros de paginação
         * @return modelo paginado com os usuários encontrados
         */
        @GetMapping
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Listagem feita com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Lista não encontrada"),
        })
        public PagedModel<EntityModel<Object>> index(@RequestParam(required = false) String busca,
                        @ParameterObject Pageable pageable) {
                log.info("Listando todos os usuário com paginação");
                Page<Usuario> usuarios = usuarioService.findUsuarios(busca, pageable);
                return assembler.toModel(usuarios.map(Usuario::toEntityModel));
        }

        /**
         * Busca os dados de um usuário por ID.
         *
         * @param id identificador do usuário
         * @return modelo da entidade usuário
         */
        @GetMapping("{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Detalhes usuário", description = "Retorna o usuário cadastrado com o id informado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Os dados foram retornados com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Não foi encontrado um usuário com esse id"),
        })
        public EntityModel<Usuario> show(@PathVariable Long id) {
                log.info("Buscar usuário por id: {}", id);
                Usuario usuario = usuarioService.findUsuarioById(id);
                return usuario.toEntityModel();
        }

        /**
         * Busca usuário pelo email.
         *
         * @param email e-mail do usuário
         * @return usuário encontrado ou 404 se não existir
         */
        @GetMapping("/email")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Encontrar usuário pelo email", description = "Retorna o usuário cadastrado com o email informado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Os dados foram retornados com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Não foi encontrado um usuário com esse email"),
        })
        public ResponseEntity<Usuario> buscaUsuarioPorEmail(@RequestParam String email) {
                log.info("Buscar usuario por email: {}", email);
                return usuarioService.findUsuarioByEmail(email)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        /**
         * Realiza o cadastro de um novo usuário.
         *
         * @param usuario dados do usuário
         * @return usuário criado com link de auto referência
         */
        @PostMapping("/cadastro")
        @Operation(summary = "Cadastrar usuário", description = "Cadastrando o usuário com os campos requisitados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuário criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Usuário invalidos"),
                        @ApiResponse(responseCode = "409", description = "Já existe um usuário com o e-mail fornecido"),
        })
        public ResponseEntity<Object> create(@RequestBody @Valid Usuario usuario) {
                log.info("Cadastrando usuario: {}", usuario);
                Usuario createdUser = usuarioService.createUsuario(usuario);
                return ResponseEntity.created(createdUser.toEntityModel().getRequiredLink("self").toUri())
                                .body(createdUser.toEntityModel());
        }

        /**
         * Exclui um usuário pelo ID.
         *
         * @param id identificador do usuário
         * @return resposta sem conteúdo
         */
        @DeleteMapping("{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Excluindo usuário", description = "Exclui o usuário cadastrado com o id informado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Requisição bem-sucedida"),
                        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        })
        public ResponseEntity<Object> delete(@PathVariable Long id) {
                log.info("Excluindo usuário por id: {}", id);
                usuarioService.deleteUsuario(id);
                return ResponseEntity.noContent().build();
        }

        /**
         * Atualiza parcialmente os dados de um usuário.
         *
         * @param id      identificador do usuário
         * @param updates mapa com os campos e valores a atualizar
         * @return usuário atualizado
         */
        @PatchMapping("{id}")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Atualizar dados do usuário parcialmente", description = "Atualiza apenas os campos fornecidos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Alteração realizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Alteração inválida"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        })
        public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
                log.info("Atualizando dados do usuário parcialmente por id: {}", id);
                Usuario updatedUser = usuarioService.updateUsuarioParcial(id, updates);
                return ResponseEntity.ok(updatedUser);
        }

        /**
         * Retorna a foto do usuário em formato JPEG.
         *
         * @param id identificador do usuário
         * @return imagem da foto ou 404 caso não exista
         */
        @GetMapping("/{id}/foto")
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Obter foto do usuário", description = "Retorna a foto do usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Foto retornada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Usuário ou foto não encontrados"),
        })
        public ResponseEntity<byte[]> getFoto(@PathVariable Long id) {
                log.info("Buscando foto do usuário com id: {}", id);

                Usuario usuario = usuarioService.findUsuarioById(id);

                if (usuario.getFoto() == null) {
                        return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok()
                                .contentType(MediaType.IMAGE_JPEG)
                                .body(usuario.getFoto());
        }

        /**
         * Atualiza a foto do usuário.
         *
         * @param id   identificador do usuário
         * @param file arquivo da nova foto
         * @return usuário com a nova foto
         * @throws IOException se houver erro na leitura do arquivo
         */
        @PatchMapping(value = "{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @SecurityRequirement(name = "bearer-key")
        @Operation(summary = "Atualizar foto do usuário", description = "Permite atualizar a foto do usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Foto alterada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        })
        public ResponseEntity<Usuario> uploadFoto(
                        @PathVariable Long id,
                        @RequestParam("foto") MultipartFile file) throws IOException {
                log.info("Atualizando foto do usuário com id: {}", id);

                if (file.isEmpty()) {
                        return ResponseEntity.badRequest().build();
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("foto", file.getBytes());

                Usuario updatedUser = usuarioService.updateUsuarioParcial(id, updates);

                return ResponseEntity.ok(updatedUser);
        }

        /**
         * Realiza o login do usuário com as credenciais fornecidas.
         *
         * @param credencial credenciais de e-mail e senha
         * @return token de autenticação
         */
        @PostMapping("/login")
        @Operation(summary = "Login do usuário", description = "Loga o usuário retornando o token do mesmo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login realizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Campos inválidos"),
        })
        public ResponseEntity<Token> login(@RequestBody Credencial credencial) {
                log.info("Fazendo login com credenciais de usário: {}", credencial);

                Token token = usuarioService.login(credencial);
                return ResponseEntity.ok(token);
        }
}
