package br.com.rotaseca.service;

/**
 * Exceção personalizada para violações de regras de negócio no domínio Rota Seca.
 */
public class NegocioException extends Exception {
    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
