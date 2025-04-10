package br.com.contabills.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.model.Credencial;
import br.com.contabills.model.Token;
import br.com.contabills.model.Usuario;
import br.com.contabills.repository.UsuarioRepository;

/**
 * Serviço responsável pelo gerenciamento de usuários, incluindo operações de
 * cadastro, autenticação, atualização e remoção.
 *
 * Oferece suporte à busca paginada, autenticação JWT e atualização parcial
 * dos dados do usuário com criptografia de senha.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class UsuarioService {

    /**
     * Construtor padrão
     */
    public UsuarioService() {
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    /**
     * Retorna uma página de usuários com base em um termo de busca.
     * 
     * Caso o parâmetro "busca" seja nulo, retorna todos os usuários paginados.
     *
     * @param busca    - termo para filtrar pelo nome (opcional).
     * @param pageable - informações de paginação e ordenação.
     * @return {@code Page<Usuario>} - página contendo os usuários.
     */
    public Page<Usuario> findUsuarios(String busca, Pageable pageable) {
        if (busca == null) {
            return usuarioRepository.findAll(pageable);
        } else {
            return usuarioRepository.findByNomeContaining(busca, pageable);
        }
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id - ID do usuário.
     * @return Usuario - usuário encontrado.
     * @throws ResponseStatusException com status 404 caso o usuário não seja encontrado.
     */
    public Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    /**
     * Cria um novo usuário.
     * 
     * Antes de persistir, a senha do usuário é criptografada para garantir a segurança.
     *
     * @param usuario - objeto Usuario a ser criado.
     * @return Usuario - usuário criado e persistido no banco de dados.
     */
    public Usuario createUsuario(Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Exclui um usuário pelo seu ID.
     *
     * @param id - ID do usuário a ser removido.
     * @throws ResponseStatusException se o usuário não for encontrado.
     */
    public void deleteUsuario(Long id) {
        usuarioRepository.delete(findUsuarioById(id));
    }

    /**
     * Atualiza parcialmente os dados de um usuário.
     * 
     * Os campos permitidos para atualização parcial são:
     * - nome: String
     * - telefone: String
     * - data: String no formato ISO (YYYY-MM-DD) a ser convertido para LocalDate
     * - senha: String, que será criptografada
     * - foto: byte[] (representa imagem do usuário)
     *
     * @param id      - ID do usuário a ser atualizado.
     * @param updates - Map contendo os campos a serem atualizados e seus novos valores.
     * @return Usuario - usuário atualizado.
     * @throws ResponseStatusException caso algum campo possua valor inválido ou a data esteja em formato incorreto.
     */
    public Usuario updateUsuarioParcial(Long id, Map<String, Object> updates) {
        Usuario existingUser = findUsuarioById(id);

        if (updates.containsKey("nome")) {
            existingUser.setNome((String) updates.get("nome"));
        }
        if (updates.containsKey("telefone")) {
            existingUser.setTelefone((String) updates.get("telefone"));
        }
        if (updates.containsKey("data")) {
            Object dataObj = updates.get("data");
            if (dataObj instanceof String) {
                try {
                    existingUser.setData(LocalDate.parse((String) dataObj));
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inválida");
                }
            }
        }
        if (updates.containsKey("senha")) {
            existingUser.setSenha(encoder.encode((String) updates.get("senha")));
        }
        if (updates.containsKey("foto")) {
            existingUser.setFoto((byte[]) updates.get("foto"));
        }

        return usuarioRepository.save(existingUser);
    }

    /**
     * Autentica um usuário e retorna um token JWT se as credenciais forem válidas.
     * 
     * O método utiliza o AuthenticationManager para realizar a autenticação com base nas credenciais fornecidas.
     * Caso a autenticação seja bem-sucedida, gera e retorna o token.
     *
     * @param credencial - objeto contendo as credenciais do usuário (por exemplo, email e senha).
     * @return Token - objeto encapsulando o token JWT gerado.
     * @throws ResponseStatusException se a autenticação falhar.
     */
    public Token login(Credencial credencial) {
        manager.authenticate(credencial.toAuthentication());
        return tokenService.generateToken(credencial);
    }

    /**
     * Busca um usuário com base no email.
     *
     * @param email - email do usuário.
     * @return {@code Page<Usuario>} - objeto Optional contendo o usuário caso encontrado ou vazio.
     */
    public Optional<Usuario> findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
