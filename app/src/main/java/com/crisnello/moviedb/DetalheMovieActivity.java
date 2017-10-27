package com.crisnello.moviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crisnello.moviedb.config.Config;
import com.crisnello.moviedb.entitie.Movie;
import com.crisnello.moviedb.util.Util;
import com.loopj.android.image.SmartImageView;


public class DetalheMovieActivity extends AppCompatActivity {

    private TextView txt_movie_detalhe;

    private ImageView img_back;
    private SmartImageView img_movie_show;
    private Movie movie;
    private String movie_genres;

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_movie);

        util = new Util(this);

        movie = (Movie) getIntent().getSerializableExtra("MOVIE");
        movie_genres = (String) getIntent().getSerializableExtra("MOVIE_GENRES");

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txt_movie_detalhe = (TextView) findViewById(R.id.txt_movie_detalhe);


        String strShow = movie.getTitle()+
                "\n\n"+movie_genres+"\n\n"+
                movie.getRelease_date()+
                "\n\n"+
                movie.getOverview();

        txt_movie_detalhe.setText(strShow);

        img_movie_show = (SmartImageView) findViewById(R.id.img_movie_detalhe);
        img_movie_show.setImageUrl(Config.WS_URL_IMG_PATH_600_900+movie.getPoster_path());




    }
}
