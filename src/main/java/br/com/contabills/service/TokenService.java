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

@Service
public class TokenService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Token generateToken(Credencial credencial) {
        Algorithm alg = Algorithm.HMAC256("meusecret");
        var jwt = JWT.create()
                .withSubject(credencial.email())
                .withIssuer("Contabills")
                .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                .sign(alg);
        return new Token(jwt, "JWT", "Bearer");
    }

    public Usuario validate(String token) {
        Algorithm alg = Algorithm.HMAC256("meusecret");
        var email = JWT.require(alg)
                .withIssuer("Contabills")
                .build()
                .verify(token)
                .getSubject();

        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new JWTVerificationException("usuario nao encontrado"));

    }

}