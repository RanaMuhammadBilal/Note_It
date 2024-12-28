package com.bildroid.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class notesDetail extends AppCompatActivity {

    TextView contentOfNoteDetail, titleOfNoteDetail;
    FloatingActionButton floatingButtonOfNoteDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);

        titleOfNoteDetail = findViewById(R.id.titleofnoteDetail);
        contentOfNoteDetail = findViewById(R.id.contentOfnoteDetails);
        floatingButtonOfNoteDetail = findViewById(R.id.floatingButtonOfnotesDetail);

        Intent data = getIntent();

        int color = ContextCompat.getColor(this, R.color.black);
        floatingButtonOfNoteDetail.setImageTintList(ColorStateList.valueOf(color));

        floatingButtonOfNoteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notesDetail.this, editNotesActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                startActivity(intent);
                finish();
            }
        });

        titleOfNoteDetail.setText(data.getStringExtra("title"));
        contentOfNoteDetail.setText(data.getStringExtra("content"));

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(notesDetail.this, notesActivity.class);
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