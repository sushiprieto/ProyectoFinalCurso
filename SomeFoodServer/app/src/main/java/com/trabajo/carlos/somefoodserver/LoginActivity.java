package com.trabajo.carlos.somefoodserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trabajo.carlos.somefoodserver.Common.Common;
import com.trabajo.carlos.somefoodserver.Model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnLogin;

    //Iniciar firebase
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = (MaterialEditText) findViewById(R.id.login_edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.login_edtPassword);
        btnLogin = (Button) findViewById(R.id.login_btnLogin);

        //Iniciar firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser(edtPhone.getText().toString(), edtPassword.getText().toString());

            }
        });

    }

    private void loginUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Logeandose...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localPhone).exists()){

                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if (Boolean.parseBoolean(user.getIsStaff())){ //Si isStuff es verdad

                        if (user.getPassword().equals(localPassword)){

                            Intent login = new Intent(LoginActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();

                        }else{

                            Toast.makeText(LoginActivity.this, "Contraseña erronea", Toast.LENGTH_SHORT).show();

                        }


                    }else{

                        Toast.makeText(LoginActivity.this, "Por favor logeate con una cuenta de administrador", Toast.LENGTH_SHORT).show();

                    }

                }else{

                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
