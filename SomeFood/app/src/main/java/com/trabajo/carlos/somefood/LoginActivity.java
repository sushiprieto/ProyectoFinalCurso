package com.trabajo.carlos.somefood;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Utilidad.User;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnLogin;
    private CheckBox ckbRemember;
    private TextView txvForgot;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = (MaterialEditText) findViewById(R.id.login_edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.login_edtPassword);
        btnLogin = (Button) findViewById(R.id.login_btnLogin);
        ckbRemember = (CheckBox)findViewById(R.id.login_ckbRemember);
        txvForgot = (TextView)findViewById(R.id.login_txvForgotPwd);

        //Iniciar Paper
        Paper.init(this);

        //Iniciar firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Comprobamos si tiene conexion a internet
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Guardamos user y password
                    if (ckbRemember.isChecked()){

                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());

                    }

                    final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                    mDialog.setMessage("Logeandose...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Comprobar si el usuario no existe en la bbdd
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                //Recoger info del usuario
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                                //Set telefono
                                user.setPhone(edtPhone.getText().toString());

                                if (user.getPassword().equals(edtPassword.getText().toString())) {

                                    Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                    Common.currentUser = user;
                                    startActivity(home);
                                    finish();

                                } else {

                                    Toast.makeText(LoginActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();

                                }

                            } else {

                                mDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Usuario no existente", Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{

                    Toast.makeText(LoginActivity.this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
                    return;

                }

            }

        });

        txvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showForgotPwdDialog();

            }
        });

    }

    private void showForgotPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Olvidó la Contraseña");
        builder.setMessage("Introduce tu código de seguridad");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view =  inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.forgot_edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.forgot_edtSecureCode);

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Comprobamos si el usuario esta disponible
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                        if (user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(LoginActivity.this, "Tu contraseña es: " + user.getPassword(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this, "Código de seguridad incorrecto" + user.getPassword(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }
}
