package com.example.springtweet.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.springtweet.services.TweetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.springtweet.classes.Tweet;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Controlador REST responsável pelas operações relacionadas aos tweets.
 * <p>
 * Inclui endpoints para registrar, listar, buscar, atualizar e excluir tweets.
*/
@RestController
@RequestMapping("/api/tweets")
public class TweetController {
    @Autowired
    private TweetService tweetService;

    /**
     * Cria um novo tweet associado a um usuário.
     * @param userId ID do autor do tweet
     * @param tweet dados do tweet a ser criado
     * @return tweet criado
    */
   @PostMapping("/create/{userId}")
    public Tweet createTweet(@PathVariable Long userId, @RequestBody Tweet tweet) { // RF06
        return tweetService.createTweet(userId, tweet);
    }

    /**
     * Retorna todos os tweets cadastrados no sistema.
     * @return lista de todos os tweets
    */
    @GetMapping
    public List<Tweet> getAll() { // RF07
        return tweetService.findAll();
    }

    /**
     * Retorna um tweet específico pelo seu ID.
     * @param tweetId ID do tweet a ser buscado
     * @return tweet encontrado
    */
    @GetMapping("/{tweetId}")
    public Tweet getById(@PathVariable Long tweetId) { // RF08
        return tweetService.findByTId(tweetId);
    }

    /**
     * Atualiza o conteúdo de um tweet.
     * @param tweetId ID do tweet a ser atualizado
     * @param requesterId ID do usuário solicitante
     * @param newTweet novos dados do tweet
     * @return tweet atualizado
    */
    @PutMapping("/{tweetId}/update/{requesterId}")
    public Tweet updateTweet(@PathVariable Long tweetId, @PathVariable Long requesterId, @RequestBody Tweet newTweet) { // RF09
        return tweetService.updateTweet(tweetId, requesterId, newTweet);
    }

    /**
     * Exclui um tweet do sistema.
     * @param tweetId ID do tweet a ser removido
     * @param requesterId ID do usuário solicitante
     * @return mensagem de confirmação
    */
    @DeleteMapping("/{tweetId}/delete/{requesterId}")
    public String deleteTweet(@PathVariable Long tweetId, @PathVariable Long requesterId) { // RF10
        tweetService.deleteTweet(tweetId, requesterId);
        return "Tweet excluído com sucesso";
    }
}