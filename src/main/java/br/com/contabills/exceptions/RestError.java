package br.com.contabills.exceptions;

/**
 * Classe que representa um erro REST padronizado.
 * 
 * Contém um código de erro e uma mensagem descritiva.
 * Pode ser utilizada para respostas de erro em APIs.
 * 
 * 
 * @param cod código do erro HTTP
 * @param message mensagem descritiva do erro
 * 
 * @author Gerson
 * @version 1.0
 */
public record RestError(int cod, String message) {
}