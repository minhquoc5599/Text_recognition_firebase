package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn;
    Toolbar toolbar;
    static boolean count;
    ListView lvDocument;
    ArrayList<Document> arrayDocument ;
    DatabaseReference mData;
    DocumentAdapter adapter = null;
    public static final String URL_IMAGE = "URL_IMAGE";
    FirebaseAuth mAuth =FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Are you sign out ?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent comeback = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(comeback);
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    dialog.create().show();
                }
//                if(item.getItemId()==R.id.menuSearch)
//                {
//                    MenuItem myActionMenuItem = toolbar.getMenu().findItem(R.id.menuSearch);
//                    SearchView searchView = (SearchView)myActionMenuItem.getActionView();
//                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                        @Override
//                        public boolean onQueryTextSubmit(String query) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onQueryTextChange(String newText) {
//                            if(TextUtils.isEmpty(newText))
//                            {
//
//                            }
//                            return false;
//                        }
//                    });
//                }
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
                String image = arrayDocument.get(position).getImage();
                intent.putExtra(URL_IMAGE, image);
                startActivity(intent);
            }
        });
        //Delete file in list view
        lvDocument.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String image = arrayDocument.get(position).getImage();
                String name = arrayDocument.get(position).getName();
                final Query query = mData.child("Document").orderByChild("image").equalTo(image);
                assert image != null;
                final StorageReference mImage = storage.getReferenceFromUrl(image);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Do you want remove "+ name +" ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Delete(query, mImage);
                            }
                        })
                        .setNegativeButton("Cancel", null);
                dialog.create().show();
                return true;
            }
        });

    }


    private void Delete(Query query, StorageReference mImage) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    ds.getRef().removeValue();
                }
                Toast.makeText(MainActivity.this,"Xoá file thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Lỗi không thể xoá", Toast.LENGTH_SHORT).show();
            }
        });

        mImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Xoá ảnh thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadData()
    {
        mData.child("Document").orderByChild("email").equalTo(user.getEmail()).addChildEventListener(new ChildEventListener() {
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
        toolbar.inflateMenu(R.menu.menu);
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
            Toast.makeText(this, "Chạm lần nữa để thoát", Toast.LENGTH_SHORT).show();
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
