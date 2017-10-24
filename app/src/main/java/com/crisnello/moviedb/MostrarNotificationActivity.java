package com.crisnello.moviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crisnello.moviedb.util.Util;

public class MostrarNotificationActivity extends AppCompatActivity {

        private TextView  txtMessage;

    private Util myUtil;
    Button btn_ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_notification);
        myUtil = new Util(MostrarNotificationActivity.this);

        txtMessage = (TextView) findViewById(R.id.txt_push_message);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String message = getIntent().getExtras().getString("message");
        myUtil.showToast("Notification "+message);
        txtMessage.setText(message);

    }


}

