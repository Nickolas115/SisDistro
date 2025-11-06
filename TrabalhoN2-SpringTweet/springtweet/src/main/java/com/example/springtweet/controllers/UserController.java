package com.example.springtweet.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.springtweet.services.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.springtweet.classes.Users;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Controlador REST responsável pelas operações relacionadas aos usuários.
 * <p>
 * Inclui endpoints para registrar, listar, buscar, atualizar e excluir usuários.
*/
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Registra um novo usuário no sistema.
     * @param user objeto contendo as informações do novo usuário
     * @return o usuário criado
    */
    @PostMapping("/register")
    public Users registerUser(@RequestBody Users user){//RF 01
        return userService.registerUser(user);
    }

    /**
     * Retorna todos os usuários cadastrados.
     * <p>
     * Disponível apenas para administradores.
     * @param requesterId ID do usuário solicitante
     * @return lista de todos os usuários
    */
    @GetMapping("/all/{requesterId}")
    public List<Users> getAll(@PathVariable Long requesterId) { // RF02
        return userService.findAll(requesterId);
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param userId ID do usuário desejado
     * @return objeto {@link Users} correspondente
    */
    @GetMapping("/{userId}")
    public Users getByUID(@PathVariable Long userId) { // RF03
        return userService.findByUId(userId);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param userId ID do usuário a ser atualizado
     * @param newUser dados atualizados do usuário
     * @return objeto {@link Users} atualizado
    */
    @PutMapping("/{userId}")
    public Users updateUser(@PathVariable Long userId, @RequestBody Users newUser) { // RF04
        return userService.updateUser(userId, newUser);
    }

    /**
     * Exclui um usuário do sistema.
     * @param requesterId ID do usuário que solicita a exclusão
     * @param targetId ID do usuário a ser excluído
     * @return mensagem de sucesso
    */
    @DeleteMapping("/{requesterId}/delete/{targetId}")
    public String deleteUser(@PathVariable Long requesterId, @PathVariable Long targetId) { // RF05
        userService.deleteUser(requesterId, targetId);
        return "Usuário removido com sucesso";
    }   
}