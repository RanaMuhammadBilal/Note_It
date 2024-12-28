package com.bildroid.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    TextView name, emaill, date, verification;
    TextInputLayout textInputNewPassword, textInputOldPassword;
    TextInputEditText editTextNewPassword, editTextOldPassword;
    Button btnChangePassword;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.displayName);
        emaill = findViewById(R.id.displayEmail);
        date = findViewById(R.id.displayDate);
        verification = findViewById(R.id.verification);
        textInputOldPassword = findViewById(R.id.textInputOldPassword);
        textInputNewPassword = findViewById(R.id.textInputNewPassword);
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user.isEmailVerified()){
            verification.setText("Account Status : Verified");
        }else {
            verification.setText("Account Status : Not Verified");
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();

                // Validate old and new password, you may add your own validation logic

                // Change password logic
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Reauthenticate user before changing password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Password successfully reauthenticated
                                    user.updatePassword(newPassword)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Password successfully updated
                                                    Toast.makeText(Profile.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                    // Clear input fields
                                                    editTextOldPassword.setText("");
                                                    editTextNewPassword.setText("");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle failure to update password
                                                    Toast.makeText(Profile.this, "Failed to change password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to reauthenticate
                                    Toast.makeText(Profile.this, "Failed to reauthenticate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            Date creationDate = new Date(user.getMetadata().getCreationTimestamp());

            // Now you can display these values in your UI
            name.setText("Name: " + displayName);
            emaill.setText("Email: " + email);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault());
            date.setText("Account Created: " + dateFormat.format(creationDate));
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, notesActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}
