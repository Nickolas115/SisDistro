package com.example.springtweet.classes;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa um tweet postado por um usuário.
 * Cada tweet contém um conteúdo, data de postagem e autor.
*/
@Entity
@Table(name = "tweets")
public class Tweet {
    /** Identificador único do tweet. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Momento em que o tweet foi postado. */
    @Column(nullable = false)
    private LocalDateTime postTime;
    /** Conteúdo textual do tweet (limitado a 200 caracteres). */
    @Column(nullable = false, length = 200)
    private String content;
    /**
     * Usuário autor do tweet.
     * <p>
     * A anotação {@code @JsonIgnoreProperties} evita loops recursivos ao serializar o usuário associado e seus atributos relacionados.
    */
    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    @JsonIgnoreProperties({"password", "profileImage", "following", "bio", "tweets", "role"})
    private Users user;

    /** Construtor padrão */
    public Tweet(){
    }

    /**
     * Construtor com os atributos principais do tweet definidos.
     * O horário de postagem é definido automaticamente como o horário atual.
     * @param content conteúdo textual do tweet
     * @param user autor do tweet
    */
    public Tweet(String content, Users user){
        this.content = content;
        this.user = user;
        this.postTime = LocalDateTime.now();
    }
    
    //Getters e Setters
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public LocalDateTime getPostTime(){
        return postTime;
    }
    public void setPostTime(LocalDateTime postTime){
        this.postTime = postTime;
    }

    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public Users getUser(){
        return user;
    }
    public void setUser(Users user){
        this.user = user;
    }
}
