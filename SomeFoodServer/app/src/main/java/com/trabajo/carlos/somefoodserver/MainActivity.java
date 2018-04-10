package com.trabajo.carlos.somefoodserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private Button btnLogin;
    private TextView txvSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        txvSlogan = (TextView) findViewById(R.id.txvSlogan);

        //Para cambiar el tipo de letra mediante codigo
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.otf");
        txvSlogan.setTypeface(face);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);

            }
        });

    }

}
