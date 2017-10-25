package com.crisnello.moviedb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crisnello.moviedb.dao.MoviedbFirebase;
import com.crisnello.moviedb.entitie.Usuario;
import com.crisnello.moviedb.entitie.UsuarioFacebook;
import com.crisnello.moviedb.util.ConexaoInternet;
import com.crisnello.moviedb.util.PreferencesUtil;
import com.crisnello.moviedb.util.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class AutoLoginActivity extends AppCompatActivity { // implements LoaderCallbacks<Cursor> {


    private String TAG = "AutoLoginActivity";

    private Util myUtil;

    private boolean retornoLogin, userFirebaseLogado;
    private FirebaseAuth authFirebase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String facebookUserId;

    private CallbackManager callbackManager;
    private Usuario usuario;
    private UsuarioFacebook userFacebook;
    public static final int ACTIVITY_REQUEST_CODE = 1;
    private UserLoginTask mAuthTask = null;

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private View mScrollView;

    private ImageView sign_in, img_facebook, img_criar;

    private TextView txt_forgot;

    private boolean Connected = true;

    private Button btnHome;

    public boolean isConnected() {
        return Connected;
    }

    public void setConnected(boolean connected) {
        Connected = connected;
    }

    //Login constante
    private String pId;
    private String pNome, pEmail, pFacebookId;


    private ValueEventListener oneTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);

//        Log.e(TAG,"onCreate");

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
        mScrollView = findViewById(R.id.scroll_login_form);

        myUtil = new Util(AutoLoginActivity.this);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            logout();
        }else {

            showProgress(true);

            facebookUserId = getIntent().getStringExtra("FACEID");
//            FacebookSdk.sdkInitialize(getApplicationContext());
//            AppEventsLogger.activateApp(this);

            //Para sumir quando faz login
            txt_forgot = (TextView) findViewById(R.id.txt_forgot);

            txt_forgot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    goToEsqueciMinhaSenha();
                }
            });

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.crisnello.moviedb", MockPackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }

            btnHome = (Button) findViewById(R.id.btn_home);
            btnHome.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMenu();
                }
            });

            sign_in = (ImageView) findViewById(R.id.sign_in);
            sign_in.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            img_criar = (ImageView) findViewById(R.id.img_criar);
            img_criar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AutoLoginActivity.this, CriarContaActivity.class);
                    startActivity(intent);
                    finish();

                }
            });


            img_facebook = (ImageView) findViewById(R.id.img_facebook);
            img_facebook.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProgress(true);
                    LoginManager.getInstance().logInWithReadPermissions(AutoLoginActivity.this, Arrays.asList("public_profile", "email"));
                }
            });

            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    // App code
                    showProgress(false);
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    showProgress(false);
                }

            });


            authFirebase = MoviedbFirebase.getAuthFirebase();

//        else {
//            if (!userFirebaseLogado) {
//                pId = PreferencesUtil.getPref(PreferencesUtil.ID, getApplicationContext());
//                pNome = PreferencesUtil.getPref(PreferencesUtil.NOME, getApplicationContext());
//                pEmail = PreferencesUtil.getPref(PreferencesUtil.EMAIL, getApplicationContext());
//                pFacebookId = PreferencesUtil.getPref(PreferencesUtil.FACEBOOKID, getApplicationContext());
//            }
////             Log.e("PreferencesUtil", "id :" + pId + " nome :" + pNome + " email :" + pEmail + " FacebookUserId :"+ pFacebookId);
//        }

            oneTime = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario myUsuario = dataSnapshot.getValue(Usuario.class);
                    if (myUsuario != null) {
                        pNome = myUsuario.getNome();
                        try {
                            usuario.setNome(pNome);
                        } catch (Exception e) {
                            Log.e(TAG, "Problema para setar nome", e);
                        }
                        layoutLogado();
                        goToMenu();
                    } else {
                        showProgress(true);
                        facebookUserId = PreferencesUtil.getPref(PreferencesUtil.FACEBOOKID, getApplicationContext());
                        if (facebookUserId != null)
                            goToCriarConta();
                        else
                            showProgress(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "oneTime:onCancelled", databaseError.toException());
                    showProgress(false);
                }
            };

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
//                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " ProviderId:" + user.getProviderId());
                        userFirebaseLogado = true;
                        pEmail = user.getEmail();
                        pId = user.getUid();
                        if (usuario == null)
                            usuario = new Usuario();

                        usuario.setId(pId);
                        usuario.setEmail(pEmail);

                        //autoLogin(pId);
                        MoviedbFirebase.getDatabase().getReference(getString(R.string.usuario)).child(user.getUid()).addListenerForSingleValueEvent(oneTime);

                    } else {
//                    Log.e(TAG, "onAuthStateChanged:signed_out");
                        userFirebaseLogado = false;
                        showProgress(false);
                    }


                }
            };
        }
    }

    public void logout(){

        try {authFirebase.signOut();}
        catch (Exception e) {//e.printStackTrace();
        }

        sairFechar();
        pId = null;
        finish();
    }

//    public void autoLogin(String pBin) {
//        if (!pBin.equals("-1")) {
//
//            Usuario mUsuario = new Usuario();
//            mUsuario.setId(pBin);
//            mUsuario.setNome(pNome);
//            mUsuario.setEmail(pEmail);
//            usuario = mUsuario;
//            Intent intent = new Intent(AutoLoginActivity.this, MainActivity.class);
//            intent.putExtra("USER", mUsuario);
//            intent.putExtra("FACEID", pFacebookId);
//            startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
//        }
//    }



    @Override
    public void onStart() {
//        Log.e(TAG, "onStart");
        super.onStart();
        authFirebase.addAuthStateListener(mAuthListener);

    }


    public void removerAuthListener(){
        if (mAuthListener != null) {
            authFirebase.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause Removendo Listener de Autenticação e Verificação se usuario existe");
        removerAuthListener();
        try {
            MoviedbFirebase.getDatabase().getReference(getString(R.string.usuario)).child(usuario.getId()).removeEventListener(oneTime);}
        catch(Exception e){//Log.e(TAG,e.getMessage(),e);
        }
    }

    public void goToEsqueciMinhaSenha() {
        Intent intent = new Intent(AutoLoginActivity.this, EsqueciMinhaSenhaActivity.class);
        startActivity(intent);
        finish();
    }

//    public void disconnectFromFacebook() {
//        LoginManager.getInstance().logOut();
//    }

    private void layoutLogado() {

        mPasswordView.setVisibility(View.GONE);
        sign_in.setVisibility(View.GONE);
        img_facebook.setVisibility(View.GONE);
        img_criar.setVisibility(View.GONE);
        txt_forgot.setVisibility(View.GONE);

        if (usuario != null && !usuario.getEmail().isEmpty())
            mEmailView.setText(usuario.getEmail());

        btnHome.setVisibility(View.VISIBLE);
    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
        removerAuthListener();
        facebookUserId = token.getUserId();
        PreferencesUtil.putPref(PreferencesUtil.FACEBOOKID, facebookUserId, getApplicationContext());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        MoviedbFirebase.getAuthFirebase().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {

//                            Log.w(TAG, "signInWithCredential", task.getException());
//                            myUtil.showToast("Authentication failed. "+task.getException().getMessage());
                            myUtil.showToast(getString(R.string.error_auth_facebook));
                            showProgress(false);
                        } else {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            usuario = myUtil.getUser();
                            usuario.setId(firebaseUser.getUid());
                            usuario.setEmail(firebaseUser.getEmail());
                            usuario.setNome(firebaseUser.getDisplayName());
//                            showProgress(true);
                            MoviedbFirebase.getDatabase().getReference(getString(R.string.usuario)).child(firebaseUser.getUid()).addListenerForSingleValueEvent(oneTime);
                        }
                    }
                });
    }

    public void goToCriarConta() {

//        Log.e(TAG,"facebookUserId "+facebookUserId);

        Intent intent = new Intent(AutoLoginActivity.this, CriarContaActivity.class);
        intent.putExtra("USER", usuario);
        intent.putExtra("FACEID", facebookUserId);
        startActivity(intent);
        finish();
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !myUtil.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_small_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!myUtil.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            mScrollView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void sairFechar() {
        PreferencesUtil.removePref(PreferencesUtil.NOME, getApplicationContext());
        PreferencesUtil.removePref(PreferencesUtil.EMAIL, getApplicationContext());
        PreferencesUtil.removePref(PreferencesUtil.ID, getApplicationContext());
        PreferencesUtil.removePref(PreferencesUtil.FACEBOOKID, getApplicationContext());
    }

    public void goToMenu() {

        if (usuario == null || usuario.getId().equals("-1")) {
            myUtil.showAlert("Usuario nao encontrado. Obs: AutoLogin - goToMenu");
        } else {
            try {

                myUtil.showToast("Seja bem vindo " + usuario.getNome());
                PreferencesUtil.putPref(PreferencesUtil.ID, usuario.getId(), getApplicationContext());
                PreferencesUtil.putPref(PreferencesUtil.NOME, usuario.getNome(), getApplicationContext());
                PreferencesUtil.putPref(PreferencesUtil.EMAIL, usuario.getEmail(), getApplicationContext());
                PreferencesUtil.putPref(PreferencesUtil.FACEBOOKID, facebookUserId, getApplicationContext());
                Intent intent = new Intent(AutoLoginActivity.this, MainActivity.class);
                intent.putExtra("USER", usuario);
                intent.putExtra("FACEID", facebookUserId);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                finish();

            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e("Login.onActivityResult","requestCode :"+requestCode+" resultCode : "+resultCode);
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (authFirebase != null)
                    authFirebase.signOut();
//                disconnectFromFacebook();
                finish();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            if (authFirebase != null)
                authFirebase.signOut();
//            disconnectFromFacebook();
            finish();
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!ConexaoInternet.verificaConexao(getApplicationContext())) {
                setConnected(false);
                return false;
            } else {
                setConnected(true);

//                HashMap<String, String> hash = new HashMap<String, String>();
//                hash.put("login_username", mEmail);
//                hash.put("login_password", mPassword);
//                String respJson = Internet.postHttp(Config.WS_URL_LOGIN, hash);
//                //Log.i("Login Usuario ",respJson);
//                user = new Gson().fromJson(respJson, Usuario.class);

                //Fazendo login pelo Firebase criado em crisnello@crisnello.com

                usuario = myUtil.getUser();
                usuario.setEmail(mEmail);
                usuario.setNome(myUtil.getNomeByEmail(mEmail));
                usuario.setSenha(mPassword);

                authFirebase.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            retornoLogin = true;

//                            showProgress(true);
//                            Log.e(TAG,"Mostrar "+getString(R.string.login_success));
//                            myUtil.showToast(getString(R.string.login_success));


                        } else {
                            retornoLogin = false;
//                            Log.e(TAG," Problema ao efetuar o Login", task.getException());
//                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
//                                myUtil.showToast(getString(R.string.error_incorrect_credential));
//                            else
                                myUtil.showToast(getString(R.string.error_incorrect_password_or_user));
                            //Colocando uma resposta visual clara
                            if (!isConnected()) {
                                myUtil.showAlert(getString(R.string.not_connected));
                            } else {

                                if(task.getException() instanceof FirebaseAuthInvalidUserException)
                                    mPasswordView.setError(getString(R.string.error_user_notexist));
                                else
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));

                                mPasswordView.requestFocus();
                            }

                            showProgress(false);
                        }


                    }
                });


//                if (usuario.getId().equals("-1") || usuario.getIdCliente() == -1) {
//                    return false;
//                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

//            if (success) {
//                try {
//                    layoutLogado();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                goToMenu();
//
//
//            } else {
//                if(!isConnected()){
//                    myUtil.showAlert("Você não está conectado na internet, efetue a conexão e tente novamente!");
//                }else {
//                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                    mPasswordView.requestFocus();
//                }
//            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

