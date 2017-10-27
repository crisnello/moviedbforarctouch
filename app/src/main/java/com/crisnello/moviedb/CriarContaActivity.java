package com.crisnello.moviedb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crisnello.moviedb.dao.MoviedbFirebase;
import com.crisnello.moviedb.entitie.Usuario;
import com.crisnello.moviedb.entitie.UsuarioFacebook;
import com.crisnello.moviedb.util.BitmapUtil;
import com.crisnello.moviedb.util.ConexaoInternet;
import com.crisnello.moviedb.util.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.loopj.android.image.SmartImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CriarContaActivity extends AppCompatActivity {

    private String TAG = "CriarContaActivity";

    private static SimpleDateFormat sdfFace = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat sdfBr = new SimpleDateFormat("dd/MM/yyyy");

    private boolean authCreated;
    private Usuario user;

    private boolean hasChanged=false;
    private boolean hasAvatar=false;
    private static final int SELECT_FILE_PROFILE = 0;
    private static final int CALLBACK_PERMISSION_LIBRARY= 7;
    private Uri picUri;
    private File file;

    private Button btn_cadastrar;
    private ImageView img_cad_facebook;
    private EditText edtNome, edtEmail, edtFone, edtData, edtFacul, edtSenha, edtRepitaSenha;

    private FirebaseAuth authFirebase;
    private DatabaseReference refFirebase;
    private FirebaseStorage storageFirebase;

    private CallbackManager callbackManager;
    private UsuarioFacebook userFacebook;
    private String facebookUserId;
    private SmartImageView smartImageCad;

    private View mProgressView;
    private View mCriarFormView;

    private View ll_show_img;
    private Util myUtil;

    private EditText edtSexo;

    private TextView txt_titulo2, txt_titulo;

    //cad
   private String cNome;
    private String cEmail;
    private  String cFone;
    private  String cSexo;
    private  String cData ;
    private  String cFacul;
    private  String cSenha;
    private String cRepitaSenha ;

    public void selecionarSexo(){
        final CharSequence[] items;
        items = new CharSequence[]{"Feminino", "Masculino"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CriarContaActivity.this);
        builder.setTitle("Selecione o sexo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                edtSexo.setText(items[item]);
                edtData.requestFocus();
                dialog.dismiss();

            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.crisnello.moviedb.R.layout.activity_criar_conta);

        mCriarFormView = (LinearLayout) findViewById(R.id.ll_criar_conta);
        mProgressView = findViewById(R.id.criar_progress);

        txt_titulo2 = (TextView) findViewById(R.id.txt_titulo2);
        txt_titulo = (TextView) findViewById(R.id.txt_titulo);

        myUtil = new Util(CriarContaActivity.this);

        edtSexo = (EditText) findViewById(R.id.cad_sexo);
        edtSexo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    selecionarSexo();
                }
            }
        });

        edtNome = (EditText) findViewById(R.id.cad_nome);
        edtEmail = (EditText) findViewById(R.id.cad_email);
        edtFone = (EditText) findViewById(R.id.cad_fone);
        edtData = (EditText) findViewById(R.id.cad_data);
        edtFacul = (EditText) findViewById(R.id.cad_facu);
        edtSenha = (EditText) findViewById(R.id.cad_senha);
        edtRepitaSenha = (EditText) findViewById(R.id.cad_repitasenha);

        smartImageCad = (SmartImageView) findViewById(R.id.si_upload);

        btn_cadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true);


                cNome = edtNome.getText().toString();
                cEmail = edtEmail.getText().toString();
                cFone = edtFone.getText().toString();
                cSexo = edtSexo.getText().toString();
                cData = edtData.getText().toString();
                cFacul = edtFacul.getText().toString();
                cSenha = edtSenha.getText().toString();
                cRepitaSenha = edtRepitaSenha.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(cEmail)) {
                    edtEmail.setError(getString(R.string.error_field_required));
                    focusView = edtEmail;
                    cancel = true;
                } else if (!myUtil.isEmailValid(cEmail)) {
                    edtEmail.setError(getString(R.string.error_invalid_email));
                    focusView = edtEmail;
                    cancel = true;
                }

                if(!authCreated) { //nao valida senha quando cadastra pelo facebook
                    if (TextUtils.isEmpty(cSenha) || !myUtil.isPasswordValid(cSenha)) {
                        edtSenha.setError(getString(R.string.error_invalid_password));
                        focusView = edtSenha;
                        cancel = true;
                    } else if (!cSenha.equals(cRepitaSenha)) {
                        edtSenha.setError(getString(R.string.error_password_notequal));
                        focusView = edtSenha;
                        cancel = true;
                    }
                }else{
                    //Esta senha nao será cadastrada
                    cSenha = "123456";  //utizada apenas para passar o cadastro quando vem pelo facebook
                }

                if(TextUtils.isEmpty(cNome)){
                    edtNome.setError(getString(R.string.error_field_required));
                    focusView = edtNome;
                    cancel = true;
                }

                if(!TextUtils.isEmpty(cData)){
                    try{
                        Date pDate = sdfBr.parse(cData);
                    }catch(Exception e){
                        edtData.setError(getString(R.string.error_invalid_date));
                        focusView = edtData;
                        cancel = true;
                    }
                }else{
                    edtData.setError(getString(R.string.error_invalid_date));
                    focusView = edtData;
                    cancel = true;
                }

                if(TextUtils.isEmpty(cSexo)){
                    edtSexo.setError(getString(R.string.error_invalid_sexo));
                    focusView = edtSexo;
                    cancel = true;
                }else{

                    if(cSexo.substring(0,1).toLowerCase().equals("f")){
                        cSexo = "Feminino";
                    }else if(cSexo.substring(0,1).toLowerCase().equals("m")){
                        cSexo = "Masculino";
                    }else{
                        edtSexo.setError(getString(R.string.error_invalid_sexo));
                        focusView = edtSexo;
                        cancel = true;
                    }
                }

                if(TextUtils.isEmpty(cFone)){
                    edtFone.setError(getString(R.string.error_field_required));
                    focusView = edtFone;
                    cancel = true;
                }else{
                    if(cFone.length() < 11){
                        edtFone.setError(getString(R.string.error_field_fone));
                        focusView = edtFone;
                        cancel = true;
                    }
                }

                if (cancel) {
                    focusView.requestFocus();
                    showProgress(false);
                } else {
                    //Cadastrar Email/Senha para autenticação no firebase
                    authFirebase = MoviedbFirebase.getAuthFirebase();
//                    Log.e(TAG,"Email :"+cEmail+ " Senha :"+cSenha); //salvando email e senha para autenticacao
                    authFirebase.createUserWithEmailAndPassword(cEmail,cSenha).addOnCompleteListener(CriarContaActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
//                                myUtil.showToast(getString(R.string.msg_auth_user));
                                FirebaseUser firebaseUser = task.getResult().getUser();
                                inserirUsuario(firebaseUser.getUid());
                            }else{

                                View pFocus = null;

                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    if(authCreated){
                                        try{
                                            inserirUsuario(user.getId());
                                        }catch(Exception e){
                                            Log.e(TAG,e.getMessage(),e);
                                            myUtil.showToast(getString(R.string.error_cad_incorrect) +" : "+ e.getMessage());
                                        }
                                    }else{
                                        myUtil.showToast(getString(R.string.error_collision_user));
                                        edtEmail.setError(getString(R.string.error_duplicate_user));
                                        pFocus = edtEmail;
                                    }
                                }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    edtEmail.setError(getString(R.string.error_incorrect_password_or_user));
                                    pFocus = edtEmail;
                                }else if(task.getException() instanceof FirebaseAuthWeakPasswordException){
                                    myUtil.showToast(getString(R.string.error_invalid_password));
                                    edtSenha.setError(getString(R.string.error_small_password));
                                    pFocus = edtSenha;
                                }else{
                                    myUtil.showToast(getString(R.string.error_cad_incorrect) +" : "+ task.getException().getMessage());
                                }
                                if(pFocus != null) {
                                    pFocus.requestFocus();
                                    showProgress(false);
                                }

                            }


                        }
                    });

                }

            }
        });

        ll_show_img = (LinearLayout) findViewById(R.id.ll_show_img);
        ll_show_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImage();
            }
        });



        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                executeGraphRequest(loginResult.getAccessToken().getUserId());
            }
            @Override
            public void onCancel() {
//                Log.e(TAG,"onCancel");
            }
            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });

        img_cad_facebook = (ImageView) findViewById(R.id.img_cad_facebook);
        img_cad_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(CriarContaActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
            }
        });


        user = (Usuario) getIntent().getSerializableExtra("USER");

        facebookUserId = getIntent().getStringExtra("FACEID");
        if(facebookUserId != null && !facebookUserId.isEmpty()){
            authCreated = true;
            executeGraphRequest(facebookUserId);
        }else{
            authCreated = false;
        }

    }

    private void inserirUsuario(String firebaseUserUid){
        Usuario usuario = new Usuario();
        usuario.setId(firebaseUserUid);
        usuario.setEmail(cEmail);
        usuario.setNome(cNome);
        //usuario.setSenha(cSenha);
        usuario.setData(cData);
        usuario.setTelefone(cFone);
        usuario.setFacul(cFacul);
        usuario.setSexo(cSexo);

        //Salvando dados do usuário
        refFirebase = MoviedbFirebase.getRefFirebase();
        refFirebase.child(getString(R.string.usuario)).child(usuario.getId()).setValue(usuario);
//                                myUtil.showToast(getString(R.string.msg_create_user));

        storageFirebase = MoviedbFirebase.getStorageFirebase();
        StorageReference storageRef = storageFirebase.getReferenceFromUrl(getString(R.string.ref_from_url));
        StorageReference profileRef = storageRef.child(getString(R.string.dir_img_profile)+usuario.getId()+getString(R.string.img_extension));

        smartImageCad.setDrawingCacheEnabled(true);
        smartImageCad.buildDrawingCache();
        Bitmap bitmap = smartImageCad.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        //salvando foto do usuario
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                myUtil.showToast(getString(R.string.error_img_profile));
                login();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                myUtil.showToast(getString(R.string.msg_insert_user));
                login();
            }
        });
    }

    public void login(){

//        Log.e(TAG,"facebookUserId "+facebookUserId);

        Intent intent = new Intent(CriarContaActivity.this, AutoLoginActivity.class);
        intent.putExtra("FACEID", facebookUserId);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        login();
    }


    private void carregaFoto(){
        try {
            if(facebookUserId != null && !facebookUserId.isEmpty()) {
                String fotoFaceURL = myUtil.recuperaFotoPerfilFacebook(facebookUserId);
//                Log.e(TAG,"FACEBOOK URL IMG : "+fotoFaceURL);
                smartImageCad.setImageUrl(fotoFaceURL);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void selectFromLibrary(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), SELECT_FILE_PROFILE);
    }

    private void selecionarImage() {

        final CharSequence[] items;
        if(hasAvatar) {
                items = new CharSequence[]{"Galeria", "Deletar Foto", "Cancelar"};
        }
        else {
                items = new CharSequence[]{"Galeria", "Cancelar"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Galeria")) {

                    if (ContextCompat.checkSelfPermission(CriarContaActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(CriarContaActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(CriarContaActivity.this,
                                new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                CALLBACK_PERMISSION_LIBRARY);
                    }else
                    {
                        selectFromLibrary();
                    }

                } else if (items[item].equals("Deletar Avatar")) {
                    smartImageCad.setImageResource(R.mipmap.ic_launcher);

                    hasAvatar = false;
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });



        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_FILE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                picUri = data.getData();
                getPictureFromClipData(picUri);
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getPictureFromClipData(Uri uri) {

        ContentResolver cr = this.getContentResolver();
        String type = cr.getType(uri);

        Bitmap thumb=null;

        if(type!=null && !type.equals(""))
        {
            if(uri.getPath().contains("ACTUAL"))
            {
                String unusablePath = uri.getPath();
                int startIndex = unusablePath.indexOf("external/");
                int endIndex = unusablePath.indexOf("/ACTUAL");
                String embeddedPath = unusablePath.substring(startIndex, endIndex);

                Uri.Builder builder = uri.buildUpon();
                builder.path(embeddedPath);
                builder.authority("media");
                uri = builder.build();
            }


            Cursor cursor=null;
            try {
                String[] projection = { MediaStore.Images.Media._ID };
                cursor = managedQuery(uri, projection, null, null, null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                cursor.moveToFirst();
                long imageId = cursor.getLong(column_index);

                thumb =  MediaStore.Images.Thumbnails.getThumbnail(
                        getContentResolver(), imageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null);

                String[] _projection = { MediaStore.Images.Media.DATA };
                cursor = this.managedQuery(uri, _projection, null, null, null);
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                final String path =cursor.getString(column_index);

                file= new File(path);

                thumb= BitmapUtil.getRoundedBitmap(thumb,1000,getResources().getColor(R.color.gray_dark));
                smartImageCad.setImageBitmap(thumb);
                smartImageCad.setScaleType(ImageView.ScaleType.CENTER_CROP);


            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }else
        {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 2;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath().toString(), options);


                bitmap=BitmapUtil.getRoundedBitmap(bitmap,1000,getResources().getColor(R.color.gray_dark));
                smartImageCad.setImageBitmap(bitmap);
                smartImageCad.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        hasAvatar=true;
        hasChanged=true;
    }

    private void executeGraphRequest(final String userId){
        GraphRequest request =new GraphRequest(AccessToken.getCurrentAccessToken(), userId, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                //Log.i("FACEBOOK", Profile.getCurrentProfile().toString());
                String respFacebookJson = response.getJSONObject().toString();
//                Log.e("Login FaceBook Usuario", respFacebookJson);
                userFacebook = new Gson().fromJson(respFacebookJson, UsuarioFacebook.class);
//                Log.e("UsuarioFacebook",userFacebook.toString());

                if(!ConexaoInternet.verificaConexao(getApplicationContext())){
                    myUtil.showAlert("Você não está conectado na internet, efetue a conexão e tente novamente!");
                }else {

                    facebookUserId = userId;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                edtNome.setText(userFacebook.getName());
                                edtEmail.setText(userFacebook.getEmail());
                                //TODO
                                if(authCreated) {
                                    edtSenha.setVisibility(View.GONE);
                                    edtRepitaSenha.setVisibility(View.GONE);
                                    edtEmail.setEnabled(false);
                                    img_cad_facebook.setVisibility(View.GONE);
                                    txt_titulo.setVisibility(View.GONE);
                                    txt_titulo2.setText(getString(R.string.msg_cad_complete));
                                }

                                Date iDate = sdfFace.parse(userFacebook.getBirthday());
                                edtData.setText(sdfBr.format(iDate));

                                    String sexo = userFacebook.getGender();
                                    if(sexo.substring(0,1).toLowerCase().equals("f")){
                                        sexo = "Feminino";
                                    }else if(sexo.substring(0,1).toLowerCase().equals("m")){
                                        sexo = "Masculino";
                                    }

                                edtSexo.setText(sexo);
    //                            myUtil.showToast("Id Facebook : "+userFacebook.getId());
                                carregaFoto();

                                edtFone.requestFocus();
                            }catch(Exception e){
                                Log.e("executeGraphRequest",e.getMessage());
                            }
                        }
                    });

                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, birthday, gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCriarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCriarFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCriarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCriarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
