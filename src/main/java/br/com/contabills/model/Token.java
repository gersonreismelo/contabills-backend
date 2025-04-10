package br.com.contabills.model;

/**
 * Representa um token de autenticação.
 *
 * Este record encapsula os dados retornados após uma autenticação bem-sucedida.
 * Pode ser utilizado em sistemas que implementam autenticação via JWT ou outro tipo de token bearer.
 *
 * @param token  O valor do token de acesso.
 * @param type   O tipo do token (ex: "Bearer", "JWT").
 * @param prefix Prefixo a ser utilizado no header (ex: "Authorization: Bearer ...").
 *
 * @author Gerson
 * @version 1.0
 */
public record Token(
        String token,
        String type,
        String prefix) {
}
