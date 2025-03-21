package br.com.contabills.model;

public record Token(
        String token,
        String type,
        String prefix) {
}
