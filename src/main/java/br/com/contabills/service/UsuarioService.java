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

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    public Page<Usuario> findUsuarios(String busca, Pageable pageable) {
        if (busca == null) {
            return usuarioRepository.findAll(pageable);
        } else {
            return usuarioRepository.findByNomeContaining(busca, pageable);
        }
    }

    public Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Usuario createUsuario(Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.delete(findUsuarioById(id));
    }

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

    public Token login(Credencial credencial) {
        manager.authenticate(credencial.toAuthentication());
        return tokenService.generateToken(credencial);
    }

    public Optional<Usuario> findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
