package com.example.springtweet.classes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Representa um usuário da aplicação SpringTweet.
 * Cada usuário pode criar tweets e seguir outros usuários.
*/
@Entity
@Table(name = "users")
public class Users {
    /** Identificador único do usuário. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    /** Senha do usuário utilizada para autenticação. */
    @Column(nullable = false)
    private String password;
    /** Nome público exibido nas interações. */
    @Column(nullable = false, unique = true, length = 50)
    private String screenName;
    /** Caminho ou URL da imagem de perfil do usuário. */
    private String profileImage;
    /**
     * Lista de usuários seguidos por este usuário. Um usuário pode seguir vários outros.
     * <p>
     * A anotação {@code @JsonIgnoreProperties} evita loops recursivos ao serializar para JSON, ignorando os atributos listados nos objetos de usuários seguidos.
    */
    @ManyToMany
    @JoinTable(name = "following", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    @JsonIgnoreProperties({"password", "screenName", "profileImage", "following", "bio", "tweets", "role"})
    private List<Users> following;
    /** Biografia curta exibida no perfil do usuário. */
    @Column(length = 300)
    private String bio;
    /**
     * Lista de tweets feitos por este usuário. Um usuário pode ter vários tweets
     * 
    */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> tweets;
    /** Papel do usuário no sistema (ADMIN ou USER). */
    @Column(nullable = false)
    private String role;

    /** Construtor padrão */
    public Users(){
    }

    /**
     * Construtor com os atributos principais do usuário definidos.
     * @param screenName nome público do usuário
     * @param password senha de autenticação
     * @param bio biografia do usuário
     * @param profileImage imagem de perfil (URL)
     * @param role função do usuário (ADMIN ou USER)
    */
    public Users(String screenName, String password, String bio, String profileImage, String role){
        this.screenName = screenName;
        this.password = password;
        this.bio = bio;
        this.profileImage = profileImage;
        this.role = role;
    }

    /**
     * Verifica se o usuário possui o papel de administrador.
     * @return {@code true} se o usuário for ADMIN, caso contrário {@code false}.
    */
    public boolean isAdmin(){
        return "ADMIN".equalsIgnoreCase(role);
    }

    //Getters e Setters
    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getScreenName(){
        return screenName;
    }
    public void setScreenName(String screenName){
        this.screenName = screenName;
    }

    public String getProfileImage(){
        return profileImage;
    }
    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public List<Users> getFollowing(){
        return following;
    }
    public void setFollowing(List<Users> following){
        this.following = following;
    }

    public String getBio(){
        return bio;
    }
    public void setBio(String bio){
        this.bio = bio;
    }

    public List<Tweet> getTweets(){
        return tweets;
    }
    public void setTweets(List<Tweet> tweets){
        this.tweets = tweets;
    }

    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role = role.toUpperCase(); // Converte para maiuscula
    }
}
