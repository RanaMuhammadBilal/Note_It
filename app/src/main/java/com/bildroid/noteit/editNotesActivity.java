package com.bildroid.noteit;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class editNotesActivity extends AppCompatActivity {

    EditText titleOfEditNotes, contentOfEditNotes;
    FloatingActionButton floatingUpdateButtonOfEditNotes;
    ImageButton imageButton;
    Intent data;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        titleOfEditNotes = findViewById(R.id.titleOfeditNotes);
        contentOfEditNotes = findViewById(R.id.contentOfeditNotes);
        floatingUpdateButtonOfEditNotes = findViewById(R.id.floatingupdateButtonOfeditNotes);
        imageButton = findViewById(R.id.backButtonEdit);
        data = getIntent();

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        titleOfEditNotes.setText(noteTitle);
        contentOfEditNotes.setText(noteContent);

        int color = ContextCompat.getColor(this, R.color.black);
        floatingUpdateButtonOfEditNotes.setImageTintList(ColorStateList.valueOf(color));

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editNotesActivity.this, notesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        floatingUpdateButtonOfEditNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(editNotesActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                
                String newTitle = titleOfEditNotes.getText().toString();
                String newContent = contentOfEditNotes.getText().toString();
                if (newTitle.isEmpty() || newContent.isEmpty()){
                    Toast.makeText(editNotesActivity.this, "Please fill the empty fields", Toast.LENGTH_SHORT).show();
                }else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content", newContent);
                    note.put("timestamp", FieldValue.serverTimestamp());
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(editNotesActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editNotesActivity.this, notesActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editNotesActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });




    }

    static String timeStampToString(Timestamp timestamp) {
        // Use "dd/MM/yyyy, HH:mm" to get the desired date and time format
        String dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(timestamp.toDate());
        return dateFormat;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(editNotesActivity.this, notesActivity.class);
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