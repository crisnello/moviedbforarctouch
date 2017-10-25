package com.crisnello.moviedb.entitie;

import java.io.Serializable;

/**
 * Created by crisnello
 */

public class Genre implements Serializable {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        Genre gObj = (Genre)obj;
        if(gObj.getId() == getId())
            return true;
        else
            return false;
    }
}
