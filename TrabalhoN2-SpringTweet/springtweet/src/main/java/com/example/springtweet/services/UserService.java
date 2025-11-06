package com.example.springtweet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springtweet.repositories.UserRepository;
import com.example.springtweet.classes.Users;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos usuários.
 * <p>
 * Inclui funcionalidades de cadastro, listagem, atualização e exclusão de usuários, com controle de permissões para ações administrativas.
*/
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Registra um novo usuário no sistema.
     * <p>
     * Verifica se o nome de tela é válido e se não há duplicidade.
     * @param user objeto {@link Users} contendo os dados do novo usuário
     * @return o usuário criado e persistido no banco de dados
     * @throws RuntimeException se o nome estiver vazio ou já for utilizado
    */
    public Users registerUser(Users user){//RF 01
        if(user.getScreenName() == null || user.getScreenName().trim().isEmpty()){
            throw new RuntimeException("O nome não pode estar vazio");
        }
        if(userRepository.existsByScreenName(user.getScreenName())){
            throw new RuntimeException("Nome já está em uso");
        }
        return userRepository.save(user);
    }
    
    /**
     * Retorna todos os usuários cadastrados.
     * <p>
     * Disponível apenas para administradores.
     * @param requesterId ID do usuário solicitante
     * @return lista de todos os usuários cadastrados
     * @throws RuntimeException se o solicitante não for administrador
    */
    public List<Users> findAll(Long requesterId){//RF 02, somente admin
        Users user = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if(!user.isAdmin()){//Verifica se é admin
            throw new RuntimeException("Apenas administradores podem usar esta função");
        }
        return userRepository.findAll();
    }
    
    /**
     * Busca um usuário pelo seu ID.
     * @param userId ID do usuário a ser encontrado
     * @return o usuário correspondente
     * @throws RuntimeException se o usuário não for encontrado
    */
    public Users findByUId(Long userId){//RF 03
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    /**
     * Atualiza os dados de um usuário.
     * <p>
     * Apenas o próprio usuário pode alterar suas informações.
     * @param userId ID do usuário a ser atualizado
     * @param newUser objeto {@link Users} contendo os novos dados
     * @return usuário atualizado e salvo
     * @throws RuntimeException se o usuário não for encontrado
    */
    public Users updateUser(Long userId, Users newUser){//RF 04, somente mesmo usuário
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if(newUser.getScreenName() != null && !newUser.getScreenName().trim().isEmpty()){
            user.setScreenName(newUser.getScreenName());
        }
        if(newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()){ 
            user.setPassword(newUser.getPassword());
        }
        if(newUser.getBio() != null){
            user.setBio(newUser.getBio());
        }
        if(newUser.getProfileImage() != null){
            user.setProfileImage(newUser.getProfileImage());
        }
        if(newUser.getRole() != null){
            user.setRole(newUser.getRole());
        }
        if(newUser.getFollowing() != null && !newUser.getFollowing().isEmpty()){
            user.setFollowing(newUser.getFollowing());
        }
        return userRepository.save(user);
    }
    
    /**
     * Exclui um usuário do sistema.
     * <p>
     * A exclusão pode ser feita apenas pelo próprio usuário ou por um administrador.
     * @param requesterId ID do usuário que solicita a exclusão
     * @param targetId ID do usuário a ser excluído
     * @throws RuntimeException se o solicitante não tiver permissão ou se o usuário não existir
    */
    public void deleteUser(Long requesterId, Long targetId){//RF 05, somente admin ou mesmo usuário
        Users requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Users target = userRepository.findById(targetId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!requester.isAdmin() && !requester.getUserId().equals(target.getUserId())) {
            throw new RuntimeException("Usuário não tem permissões suficientes");
        }
        userRepository.delete(target);
    }
}
