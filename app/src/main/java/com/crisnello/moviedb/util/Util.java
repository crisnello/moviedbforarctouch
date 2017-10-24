package com.crisnello.moviedb.util;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.crisnello.moviedb.entitie.Usuario;

import java.net.MalformedURLException;


/**
 * Created by crisnello
 */

public class Util {

    private Context context;

    public Util(Context pContext){
        context = pContext;

    }

    public void showToast(String pMsg){
        Toast.makeText(context, pMsg, Toast.LENGTH_LONG).show();
    }


    public void showAlert(String pMsg){
        CustomAlert alert = new CustomAlert(context);
        alert.setMessage(pMsg);
        alert.show();
    }

    public Usuario getUser(){
        Usuario usuario = new Usuario();
        usuario.setId("fake");
        usuario.setIdCliente(99);
        usuario.setEmail("crisnello@gmail.com");
        usuario.setNome("crisnello");
        return usuario;
    }

    public String getNomeByEmail(String pEmail){
        String[] strings = pEmail.split("@");
        return strings[0];
    }

    public String recuperaFotoPerfilFacebook(String userID) throws MalformedURLException {
        Uri.Builder builder = Uri.parse("https://graph.facebook.com").buildUpon();
        builder.appendPath(userID).appendPath("picture").appendQueryParameter("type", "large"); //small
        return builder.toString();
    }


    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

}
