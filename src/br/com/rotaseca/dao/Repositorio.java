package br.com.rotaseca.dao;

import java.util.List;

/**
 * Interface genérica para Repositórios (DAOs), padronizando operações de CRUD.
 * @param <T> Tipo da entidade do domínio
 */
public interface Repositorio<T> {
    T inserir(T entidade);
    boolean atualizar(T entidade);
    boolean excluir(Long id);
    T buscarPorId(Long id);
    List<T> listarTodos();
}
