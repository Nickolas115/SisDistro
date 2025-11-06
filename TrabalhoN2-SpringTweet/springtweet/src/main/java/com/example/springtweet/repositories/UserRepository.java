package com.example.springtweet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springtweet.classes.Users;

/**
 * Repositório responsável pelo acesso aos dados da entidade {@link Users}.
 * <p>
 * Fornece operações básicas de CRUD e métodos personalizados de consulta.
*/
public interface UserRepository extends JpaRepository<Users, Long> {    
    /**
     * Verifica se já existe um usuário cadastrado com o nome especificado.
     * @param screenName nome de tela do usuário
     * @return {@code true} se existir um usuário com esse nome, caso contrário {@code false}
    */
    boolean existsByScreenName(String screenName);
}