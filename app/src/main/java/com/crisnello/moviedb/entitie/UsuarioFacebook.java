package com.crisnello.moviedb.entitie;

import java.io.Serializable;

/**
 * Created by crisnello
 */

public class UsuarioFacebook implements Serializable {

    private long id;
    private String name;
    private String email;
    private String birthday;
    private String gender;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ID :"+getId()+" Nome :"+getName()+" Email :"+getEmail();
    }
}
