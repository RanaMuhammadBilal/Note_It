package com.bildroid.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    CheckBox checkBox;
    EditText edtEmail, edtPassword;
    Button loginButton;
    TextView signUP, forgotPassword;
    FirebaseAuth firebaseAuth;
    LottieAnimationView anim_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox= findViewById(R.id.checkBox);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_Password);
        signUP = findViewById(R.id.signUp);
        loginButton = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgot);
        anim_loading = findViewById(R.id.anim_loading);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser!=null){

            finish();
            Intent intent = new Intent(MainActivity.this, notesActivity.class);
            startActivity(intent);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    edtPassword.setTransformationMethod(null);
                    edtPassword.setSelection(edtPassword.getText().length());
                }else {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtPassword.setSelection(edtPassword.getText().length());
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, fotgotPassword.class);
                startActivity(intent);
            }
        });

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentH = new Intent(MainActivity.this, SignUp.class);
                startActivity(intentH);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = edtEmail.getText().toString();
                String passord = edtPassword.getText().toString();
                if (mail.isEmpty() || passord.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    // login the user
                    anim_loading.playAnimation();
                    anim_loading.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(mail, passord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser(); // Get the authenticated user

                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, notesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Verify Your Email First", Toast.LENGTH_SHORT).show();
                                        firebaseAuth.signOut();
                                        anim_loading.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    // Handle the case where the user is null
                                    Toast.makeText(MainActivity.this, "User is null. Authentication issue.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Account Doesn't Exist or Sign-In Failed", Toast.LENGTH_SHORT).show();
                                anim_loading.setVisibility(View.INVISIBLE);
                            }
                        }
                    });


                }
            }
        });



    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder extDialog = new AlertDialog.Builder(this);

        extDialog.setIcon(R.drawable.baseline_exit_to_app_24);
        extDialog.setTitle("Exit");
        extDialog.setMessage("are you sure you want to exit?");

        extDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });
        extDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //null
            }
        });

        extDialog.show();
    }
}