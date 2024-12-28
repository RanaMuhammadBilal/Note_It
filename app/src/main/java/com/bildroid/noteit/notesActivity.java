package com.bildroid.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class notesActivity extends AppCompatActivity {

    ExtendedFloatingActionButton floatingActionButton;
    FirebaseAuth firebaseAuth;
    ImageButton menuButton;
    RecyclerView recyclerView;
    EditText search;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        floatingActionButton = findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notesActivity.this, cereteNotes.class);
                startActivity(intent);
                finish();
            }
        });

        search = findViewById(R.id.search);


        menuButton= findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.logout){
                            firebaseAuth.signOut();
                            Intent intent = new Intent(notesActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.profile){
                            Intent intent = new Intent(notesActivity.this, Profile.class);
                            startActivity(intent);
                            finish();
                            return true;
                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        //Notes Showing part

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // Handle the case when the user is not authenticated.
            // You can log an error message or redirect to the login screen.
            Log.e("notesActivity", "User not authenticated");
            return;
        }

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e("notesActivity", "Firebase user is null");
            return;
        }
        firebaseFirestore = FirebaseFirestore.getInstance();


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("timestamp",Query.Direction.DESCENDING);

        try {

            FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();
            noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
                @Override
                protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model) {

                    ImageButton popupButton;
                    popupButton = holder.itemView.findViewById(R.id.menuPopButton);

                    int colorCode = getRandomColor();
                    holder.linearLayoutNote.setBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));
                    holder.menuPopButton.setBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));

                    holder.noteTitle.setText(model.getTitle());
                    holder.noteContent.setText(model.getContent());
                    holder.noteTime.setText(cereteNotes.timeStampToString(model.timestamp));
                    holder.noteTime.setText(editNotesActivity.timeStampToString(model.timestamp));

                    String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                    holder.linearLayoutNote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(notesActivity.this, notesDetail.class);

                            intent.putExtra("title", model.getTitle());
                            intent.putExtra("content", model.getContent());
                            intent.putExtra("noteId", docId);

                            view.getContext().startActivity(intent);
                            finish();

                        }
                    });


                    popupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);

                            MenuItem editMenuItem = popupMenu.getMenu().add("Edit");
                            editMenuItem.setIcon(R.drawable.baseline_edit_24);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                popupMenu.setForceShowIcon(true);
                            }
                            editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                    Intent intent = new Intent(notesActivity.this, editNotesActivity.class);

                                    intent.putExtra("title", model.getTitle());
                                    intent.putExtra("content", model.getContent());
                                    intent.putExtra("noteId", docId);

                                    startActivity(intent);
                                    finish();
                                    return false;
                                }
                            });
                            MenuItem anotherMenuItem = popupMenu.getMenu().add("Delete");
                            anotherMenuItem.setIcon(R.drawable.baseline_delete_24); // Replace with the icon you want to use
                            anotherMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                    //Toast.makeText(notesActivity.this, "This note is deleted", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(notesActivity.this, "This note is deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(notesActivity.this, "Failed to delete this note", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return false;
                                }
                            });

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                popupMenu.setForceShowIcon(true);
                            }

                            popupMenu.show();
                        }



                    });


                }

                @NonNull
                @Override
                public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                    return new NoteViewHolder(view);
                }
                @Override
                public void onDataChanged() {
                    super.onDataChanged();
                    Log.d("FirestoreData", "Data changed. Item count: " + getItemCount());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();

            Log.e("FirestoreRecyclerOptions", "Error creating FirestoreRecyclerOptions: " + e.getMessage());

            // Handle the exception, e.g., by logging an error or showing a message to the user.
        }





        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);




    }

    public  class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView noteTitle, noteContent, noteTime;
        private LinearLayout linearLayoutNote;
        private  ImageButton menuPopButton;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.noteTitle);
            noteContent=itemView.findViewById(R.id.noteContent);
            noteTime=itemView.findViewById(R.id.timestamp);
            linearLayoutNote = itemView.findViewById(R.id.linearNote);
            menuPopButton = itemView.findViewById(R.id.menuPopButton);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor(){
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.green);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.color6);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);

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
                notesActivity.super.onBackPressed();
                finish();
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