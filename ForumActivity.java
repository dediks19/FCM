package com.gmail.thekamtis.sm;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.gmail.thekamtis.sm.Adapter.ForumAdapter;
import com.gmail.thekamtis.sm.model.ForumMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class ForumActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseUser user;
    Query query;
    FirebaseFirestore database;

    private FirestoreRecyclerAdapter<ForumMessage, ForumAdapter.MessageHolder> adapter;
    private MultiAutoCompleteTextView input;
    private ProgressBar pgBar;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat");

        FloatingActionButton btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        input = findViewById(R.id.input);
        pgBar = findViewById(R.id.loader);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userId = user.getUid();
        userName = user.getDisplayName();
        database = FirebaseFirestore.getInstance();
        query = database.collection("messages").orderBy("messageTime");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    pgBar.setVisibility(View.GONE);
                }
            }
        });

        adapter = new ForumAdapter(query, userId, ForumActivity.this);
        Log.e("Adapter Forum","Hampir sampai");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSend){
            String message = input.getText().toString();
            /*if(TextUtils.isEmpty(message)){
                Toast.makeText(ForumActivity.this, "Post is post", Toast.LENGTH_LONG).show();
                return;
            } */
            database.collection("messages").add(new ForumMessage(userName, message, userId));
            input.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }
}
