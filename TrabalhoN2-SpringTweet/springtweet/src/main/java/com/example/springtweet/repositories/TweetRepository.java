package com.example.springtweet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springtweet.classes.Tweet;


/**
 * Repositório responsável pelo gerenciamento da entidade {@link Tweet}.
 * <p>
 * Fornece as operações CRUD padrão para tweets.
*/
public interface TweetRepository extends JpaRepository<Tweet, Long> {

}
