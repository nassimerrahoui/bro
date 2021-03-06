package com.bro.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import java.util.List;
import java.util.Objects;

/**
 * @TODO verifier l'utilisation de l'attribut enemies qui semble être inutilisé
 * Represents a bro
 */
@Entity("user")
@Indexes(@Index(fields = {@Field("username"), @Field("email")}, options = @IndexOptions(unique = true)))
public class User {
    @Id
    private ObjectId id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String token = "";

    private Boolean localizable;

    @Reference
    private List<User> enemies;

    /**
     * Empty constructor for DAO
     */
    public User() {
    }

    /**
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     */
    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }


    /**
     * Encrypts a password with a sha256 algorithm using apache codecs
     */
    public void encrypt() {
        this.password = DigestUtils.sha256Hex(password);
    }


    public ObjectId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public Boolean isLocalizable() {
        return this.localizable;
    }

    public List<User> getEnemies() {
        return enemies;
    }

    public void setDefaultValue(){
        if(this.localizable == null){
            this.localizable = true;
        }

    }

    public String toString(){
        return this.firstName +" "+ this.lastName + " <" + this.email + "> " + this.username;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.email.equals(user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.email);
    }
}