package com.example.springtweet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springtweet.repositories.TweetRepository;
import com.example.springtweet.repositories.UserRepository;
import com.example.springtweet.classes.Tweet;
import com.example.springtweet.classes.Users;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelas operações e regras de negócio dos tweets.
 * <p>
 * Inclui funcionalidades de criação, listagem, atualização e exclusão, com verificação de permissões de autor e administrador.
*/
@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Cria um novo tweet associado a um usuário.
     * @param userId ID do usuário autor
     * @param tweet objeto {@link Tweet} contendo o conteúdo do tweet
     * @return o tweet criado e salvo
     * @throws RuntimeException se o usuário não for encontrado
    */
    public Tweet createTweet(Long userId, Tweet tweet) { // RF06
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        tweet.setUser(user);
        tweet.setPostTime(LocalDateTime.now());
        return tweetRepository.save(tweet);
    }

    /**
     * Retorna todos os tweets cadastrados.
     * @return lista de todos os tweets
    */
    public List<Tweet> findAll() { // RF07
        return tweetRepository.findAll();
    }

    /**
     * Busca um tweet específico pelo seu ID.
     * @param tweetId ID do tweet desejado
     * @return tweet correspondente
     * @throws RuntimeException se o tweet não for encontrado
    */
    public Tweet findByTId(Long tweetId) { // RF08
        return tweetRepository.findById(tweetId).orElseThrow(() -> new RuntimeException("Tweet não encontrado"));
    }

    /**
     * Atualiza o conteúdo de um tweet.
     * <p>
     * Apenas o autor do tweet pode modificá-lo.
     * @param tweetId ID do tweet a ser atualizado
     * @param requesterId ID do usuário solicitante
     * @param newTweet novo conteúdo a ser aplicado
     * @return tweet atualizado
     * @throws RuntimeException se o tweet ou o usuário não existirem, ou se o solicitante não for o autor
    */
    public Tweet updateTweet(Long tweetId, Long requesterId, Tweet newTweet) { // RF09, somente dono do tweet
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new RuntimeException("Tweet não encontrado"));
        Users requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!tweet.getUser().getUserId().equals(requester.getUserId())) {
            throw new RuntimeException("Usuário não tem permissão para alterar este tweet");
        }
        tweet.setContent(newTweet.getContent());
        return tweetRepository.save(tweet);
    }


    /**
     * Exclui um tweet.
     * <p>
     * A exclusão pode ser feita apenas pelo autor do tweet ou por um administrador.
     * @param tweetId ID do tweet a ser excluído
     * @param requesterId ID do usuário solicitante
     * @throws RuntimeException se o tweet ou o usuário não existirem, ou se o solicitante não tiver permissão
    */
    public void deleteTweet(Long tweetId, Long requesterId) { // RF10, somente admin ou dono 
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new RuntimeException("Tweet não encontrado"));
        Users requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!requester.isAdmin() && !tweet.getUser().getUserId().equals(requester.getUserId())) {
            throw new RuntimeException("Usuário não tem permissão para deletar este tweet");
        }
        tweetRepository.delete(tweet);
    }
}