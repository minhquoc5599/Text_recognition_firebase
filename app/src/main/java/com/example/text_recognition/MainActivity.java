package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn;

    ListView lvDocument;

    ArrayList<Document> arrayDocument;

    DocumentAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mapping();

        btn =findViewById(R.id.icAdd);

        adapter = new DocumentAdapter(this, R.layout.document_row, arrayDocument);

        lvDocument.setAdapter(adapter);

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
                Intent intent = new Intent(getBaseContext(), EditActivity.class);

                startActivity(intent);
            }
        });
    }

    private void Mapping() {
        lvDocument = findViewById(R.id.lvDocumnet);

        arrayDocument = new ArrayList<>();
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));

    }


}
