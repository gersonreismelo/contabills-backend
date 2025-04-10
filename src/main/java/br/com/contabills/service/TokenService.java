package br.com.contabills.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.contabills.model.Credencial;
import br.com.contabills.model.Token;
import br.com.contabills.model.Usuario;
import br.com.contabills.repository.UsuarioRepository;

/**
 * Serviço responsável por gerar e validar tokens JWT para autenticação de usuários.
 * 
 * O serviço também oferece a funcionalidade de validação do token e recuperação
 * do usuário correspondente.
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class TokenService {
    /**
     * Construtor padrão
     */
    public TokenService() {
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Gera um token JWT para o usuário que está realizando a autenticação.
     * 
     * O token é gerado utilizando o algoritmo HMAC256 com a chave "meusecret". O token 
     * contém o email do usuário no campo "subject", o nome da aplicação ("Contabills") 
     * como issuer, e uma data de expiração definida para 8 horas a partir do momento da geração.
     *
     * @param credencial - objeto que contém as credenciais do usuário, como o email.
     * @return Token - objeto que encapsula o token JWT gerado, o tipo ("JWT") e o esquema ("Bearer").
     */
    public Token generateToken(Credencial credencial) {
        Algorithm alg = Algorithm.HMAC256("meusecret");

        var jwt = JWT.create()
                     .withSubject(credencial.email())                   
                     .withIssuer("Contabills")                           
                     .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS)) 
                     .sign(alg);                                        

        return new Token(jwt, "JWT", "Bearer");
    }

    /**
     * Valida um token JWT e retorna o usuário associado a ele.
     * 
     * O método verifica se o token é válido (com base no algoritmo e no emissor) e extrai o email 
     * (subject) contido no token. Em seguida, busca o usuário correspondente no banco de dados.
     *
     * @param token - token JWT que será validado.
     * @return Usuario - usuário associado ao token válido.
     * @throws JWTVerificationException se o token for inválido ou se o usuário não for encontrado.
     */
    public Usuario validate(String token) {
        Algorithm alg = Algorithm.HMAC256("meusecret");

        var email = JWT.require(alg)
                       .withIssuer("Contabills")
                       .build()
                       .verify(token)
                       .getSubject();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new JWTVerificationException("Usuário não encontrado"));
    }
}
