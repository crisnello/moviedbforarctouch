package com.crisnello.moviedb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.crisnello.moviedb.dao.MoviedbFirebase;
import com.crisnello.moviedb.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueciMinhaSenhaActivity extends AppCompatActivity {

    private String TAG = "EsqueciMinhaSenha";

    private Util myUtil;

    private FirebaseAuth authFirebase;

    private ImageView img_enviar_senha;

    private EditText email_esqueci;

    private boolean enviado;

    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_minha_senha);

        myUtil = new Util(EsqueciMinhaSenhaActivity.this);

        email_esqueci = (EditText) findViewById(R.id.email_esqueci);

        mProgressView = findViewById(R.id.login_progress);

        img_enviar_senha = (ImageView) findViewById(R.id.img_enviar_senha);
        img_enviar_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviado = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(true);
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        ArrayList<String> alStr = new ArrayList<String>();
//                        alStr.add(email_esqueci.getText().toString());
//                        Object[] strObjects =alStr.toArray();
//
//                        SendEmail sendEmail = new SendEmail();
//                        try {
//                            sendEmail.sendMail("moviedb@crisnello.com","PASSWORD",strObjects,"[ moviedb ]","A senha é: ");
//                            enviado = true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                        String emailAddress = email_esqueci.getText().toString();
                        authFirebase = MoviedbFirebase.getAuthFirebase();

                        authFirebase.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            myUtil.showToast(getString(R.string.success_email_reset));
                                            voltar();
                                        }else{
                                            Log.e(TAG,"Problema ao enviar reset de senha",task.getException());
                                            myUtil.showToast(getString(R.string.error_email_reset));
                                            email_esqueci.setError(getString(R.string.user_email_removed));

                                        }
                                        showProgress(false);
                                    }
                                });

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showProgress(false);
//                                if(enviado)
//                                    (new Util(EsqueciMinhaSenhaActivity.this)).showToast("Email enviado com sucesso!");
//                            }
//                        });

                    }
                }).start();

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void voltar(){
        Intent intent = new Intent(getApplicationContext(), AutoLoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        voltar();
    }
}
