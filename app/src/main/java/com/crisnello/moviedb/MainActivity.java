package com.crisnello.moviedb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crisnello.moviedb.dao.MoviedbFirebase;
import com.crisnello.moviedb.entitie.Usuario;
import com.crisnello.moviedb.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.image.SmartImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private SmartImageView smartImage;
    private String faceId;
    private View mProgress;
    private Usuario user;
    private TextView tv_login;

    private Util myUtil;

    private DatabaseReference refUsuario;

    private StorageReference storageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Log.e(TAG,"onCreate");

        myUtil = new Util(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (Usuario) getIntent().getSerializableExtra("USER");
        View navigationViewHeader = navigationView.getHeaderView(0);
        tv_login = (TextView) navigationViewHeader.findViewById(R.id.tv_login);
        tv_login.setText(user.getEmail());

        smartImage = (SmartImageView) navigationViewHeader.findViewById(R.id.meuSmartImage);

//        faceId = getIntent().getStringExtra("FACEID");
//        try {
//            //Log.e("MinActivity","onCreate USER_ID FACEBOOK "+faceId);
//            if(faceId != null && !faceId.isEmpty()) {
//                String fotoFaceURL = myUtil.recuperaFotoPerfilFacebook(faceId);
//                smartImage.setImageUrl(fotoFaceURL);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        refUsuario = MoviedbFirebase.getDatabase().getReference(getString(R.string.usuario));


        refUsuario.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Usuario delUsuario = dataSnapshot.getValue(Usuario.class);
                if(delUsuario.getId().equals(user.getId())) {
                    MoviedbFirebase.getAuthFirebase().getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        myUtil.showToast(getString(R.string.user_removed));
                                    }else{
//                                        Log.e(TAG,"Problema para deletar autentivação do "+delUsuario.getId()+" : "+delUsuario.getEmail(),task.getException());
                                    }
                                    sair(true);
                                }
                            });
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        try {

            String img_path = getString(R.string.ref_from_url) + getString(R.string.dir_img_profile) + user.getId() + getString(R.string.img_extension);
            Log.e(TAG,"IMG Path : "+img_path);

            storageProfile = MoviedbFirebase.getStorageFirebase().getReferenceFromUrl(getString(R.string.ref_from_url) + getString(R.string.dir_img_profile) + user.getId() + getString(R.string.img_extension));
            final long ONE_MEGABYTE = 1024 * 1024;
            storageProfile.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    //Log.e(TAG,"storageProfile.onSuccess");
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    smartImage.setImageBitmap(bmp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e(TAG, "storageProfile.onFailure " + exception.getMessage());
                exception.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //sair(false);
            myUtil.showToast(getString(R.string.msg_to_out));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sair(boolean remover){
        Intent intent = new Intent(getApplicationContext(), AutoLoginActivity.class);
        if(remover) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
        }
        startActivity(intent);
        finish();
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sair) {
            sair(true);
        }else if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}