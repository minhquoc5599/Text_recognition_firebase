package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn;
    Toolbar toolbar;
    static boolean count;
    ListView lvDocument;
    ArrayList<Document> arrayDocument ;
    DatabaseReference mData;
    DocumentAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = false;

        Connect();
        arrayDocument = new ArrayList<>();
        adapter = new DocumentAdapter(this, R.layout.document_row, arrayDocument);
        lvDocument.setAdapter(adapter);
        mData = FirebaseDatabase.getInstance().getReference();

        LoadData();


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menuLogout)
                {
                    FirebaseAuth.getInstance().signOut();
                    Intent comeback = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(comeback);
                }
                return false;
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, OcrActivity.class);
                startActivity(intent);
            }
        });

        lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

    }

    private void LoadData()
    {
        mData.child("Document").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Document document = dataSnapshot.getValue(Document.class);
                arrayDocument.add(new Document(document.getName(), document.getText(), document.getImage(),document.getEmail()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Connect() {
        btn =findViewById(R.id.icAdd);
        toolbar = findViewById(R.id.toolbarMain);
        toolbar.inflateMenu(R.menu.menu_logout);
        lvDocument = findViewById(R.id.lvDocument);
    }

    @Override
    protected void onResume() {
        count = false;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(!count)
        {
            count = true;
            Toast.makeText(this, "Quay về lần nữa để thoát chương trình", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

}
