package br.com.contabills.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.contabills.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autorização responsável por interceptar requisições HTTP e extrair
 * o token JWT do cabeçalho Authorization.
 * Se o token for válido, autentica o usuário no contexto de segurança do
 * Spring.
 *
 * Executa uma vez por requisição, sendo uma extensão de
 * {@link OncePerRequestFilter}.
 * 
 * O token deve estar no formato Bearer.
 * 
 * @author Gerson
 * @version 1.0
 */
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    /**
     * Construtor padrão do filtro de autorização.
     * Utilizado pelo Spring para injetar o componente automaticamente.
     */
    public AuthorizationFilter() {
    }

    /**
     * Método principal do filtro que intercepta todas as requisições.
     * Verifica se há um token válido e autentica o usuário se for o caso.
     *
     * @param request     a requisição HTTP
     * @param response    a resposta HTTP
     * @param filterChain a cadeia de filtros
     * 
     * @throws ServletException Se ocorrer um erro interno de servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída durante o
     *                          processamento.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getToken(request);

        if (token != null) {

            var usuario = tokenService.validate(token);

            Authentication auth = new UsernamePasswordAuthenticationToken(usuario.getEmail(), null,
                    usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        filterChain.doFilter(request, response);

    }

    /**
     * Extrai o token JWT do cabeçalho Authorization da requisição HTTP.
     * 
     * @param request a requisição HTTP
     * @return o token extraído ou null se não estiver presente ou mal formatado
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.replace("Bearer ", "");
    }

}