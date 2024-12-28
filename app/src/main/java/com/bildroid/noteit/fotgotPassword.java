package com.bildroid.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class fotgotPassword extends AppCompatActivity {

    EditText emailRecover;
    Button btnRecover;
    TextView loginScreen;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotgot_password);

        emailRecover = findViewById(R.id.edt_emailR);
        btnRecover = findViewById(R.id.recover);
        loginScreen = findViewById(R.id.loginS);

        firebaseAuth = FirebaseAuth.getInstance();

        loginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fotgotPassword.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = emailRecover.getText().toString().trim();
                if (mail.isEmpty()){
                    Toast.makeText(fotgotPassword.this, "Please Enter Valid Email Address!", Toast.LENGTH_SHORT).show();
                }else {
                    //we have to send recovery password
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(fotgotPassword.this, "Email Sent, You Can Recover Your Password Using Email", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(fotgotPassword.this, MainActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(fotgotPassword.this, "Invalid Email or Account Not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}