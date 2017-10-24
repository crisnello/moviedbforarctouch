package com.crisnello.moviedb.dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by crisnello.
 */

public class MoviedbFirebase {

    private static DatabaseReference refFirebase;
    private static FirebaseAuth authFirebase;
    private static FirebaseStorage storageFirebase;
    private static FirebaseDatabase database;

    public static FirebaseDatabase getDatabase() {
        if(database == null){
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    public static DatabaseReference getRefFirebase(){

        if(refFirebase == null){
            refFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return refFirebase;
    }


    public static FirebaseAuth getAuthFirebase(){
        if(authFirebase == null){
            authFirebase = FirebaseAuth.getInstance();
        }
        return authFirebase;
    }

    public static FirebaseStorage getStorageFirebase(){
        if(storageFirebase == null){
            storageFirebase  = FirebaseStorage.getInstance();
        }
        return storageFirebase;
    }

}
