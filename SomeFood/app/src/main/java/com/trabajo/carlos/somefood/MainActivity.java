package com.trabajo.carlos.somefood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Utilidad.User;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSignUp, btnLogin;
    private TextView txvSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txvSlogan = (TextView) findViewById(R.id.txvSlogan);

        //Para cambiar el tipo de letra mediante codigo
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.otf");
        txvSlogan.setTypeface(face);

        //Iniciar Paper
        Paper.init(this);

        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Comprobamos lo recordado
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user != null && pwd != null){

            if (!user.isEmpty() && !pwd.isEmpty()){

                login(user, pwd);

            }

        }

    }

    private void login(final String phone, final String pwd) {

        //Iniciar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        //Comprobamos si tiene conexion a internet
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Logeandose...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Comprobar si el usuario no existe en la bbdd
                    if (dataSnapshot.child(phone).exists()) {

                        mDialog.dismiss();

                        //Recoger info del usuario
                        User user = dataSnapshot.child(phone).getValue(User.class);

                        //Set telefono
                        user.setPhone(phone);

                        if (user.getPassword().equals(pwd)) {

                            Intent home = new Intent(MainActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(home);
                            finish();

                        } else {

                            Toast.makeText(MainActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Usuario no existente", Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{

            Toast.makeText(MainActivity.this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
            return;

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btnSignUp:

                Intent signUp = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUp);
                break;

            case R.id.btnLogin:

                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                break;

        }

    }
}
