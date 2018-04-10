package com.trabajo.carlos.somefood;

import android.app.ProgressDialog;
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
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Utilidad.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtPassword, edtSecureCode;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.signup_edtName);
        edtPhone = (MaterialEditText) findViewById(R.id.signup_edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.signup_edtPassword);
        edtSecureCode = (MaterialEditText) findViewById(R.id.signup_edtSecureCode);
        btnSignUp = (Button) findViewById(R.id.signup_btnSignUp);

        //Iniciar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Comprobamos si tiene conexion a internet
                if (Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Registrandose...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Comprobar si el usuario no existe en la bbdd
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                Toast.makeText(SignUpActivity.this, "Usuario ya existente", Toast.LENGTH_SHORT).show();

                            } else {

                                mDialog.dismiss();

                                String name, phone, password, secureCode;
                                name = edtName.getText().toString();
                                phone = edtPhone.getText().toString();
                                password = edtPassword.getText().toString();
                                secureCode = edtSecureCode.getText().toString();

                                User user = new User(name, password, secureCode);
                                table_user.child(phone).setValue(user);

                                Toast.makeText(SignUpActivity.this, "Registrado con éxito", Toast.LENGTH_SHORT).show();

                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{

                    Toast.makeText(SignUpActivity.this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
                    return;

                }

            }

        });

    }
}
