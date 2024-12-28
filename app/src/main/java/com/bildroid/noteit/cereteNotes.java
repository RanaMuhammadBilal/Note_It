package com.bildroid.noteit;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class cereteNotes extends AppCompatActivity {

    EditText titleOfCereteNotes, contentOfCereteNotes;
    FloatingActionButton floatingSaveButtonOfCereteNotes;
    Toolbar toolbar;
    LottieAnimationView anim_loading_create;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerete_notes);

        titleOfCereteNotes = findViewById(R.id.titleOfCereteNotes);
        contentOfCereteNotes = findViewById(R.id.contentOfCereteNote);
        floatingSaveButtonOfCereteNotes = findViewById(R.id.floatingSaveButtonOfCereteNotes);

        anim_loading_create = findViewById(R.id.anim_loading_create);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        int color = ContextCompat.getColor(this, R.color.black);
        floatingSaveButtonOfCereteNotes.setImageTintList(ColorStateList.valueOf(color));

        floatingSaveButtonOfCereteNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleOfCereteNotes.getText().toString();
                String  content = contentOfCereteNotes.getText().toString();
                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(cereteNotes.this, "Both field are require", Toast.LENGTH_SHORT).show();
                }else {

                    anim_loading_create.playAnimation();
                    anim_loading_create.setVisibility(View.VISIBLE);

                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map <String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);
                    note.put("timestamp", FieldValue.serverTimestamp());




                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(cereteNotes.this, "Note create Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(cereteNotes.this, notesActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(cereteNotes.this, "Failed to create Note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            anim_loading_create.setVisibility(View.INVISIBLE);
                        }
                    });


                }
            }
        });




    }

    static String timeStampToString(Timestamp timestamp) {
        // Use "dd/MM/yyyy, HH:mm" to get the desired date and time format
        String dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm").format(timestamp.toDate());
        return dateFormat;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(cereteNotes.this, notesActivity.class);
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