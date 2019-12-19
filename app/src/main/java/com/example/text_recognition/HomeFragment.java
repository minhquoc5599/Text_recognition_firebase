package com.example.text_recognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.text_recognition.Adapter.DocumentAdapter;
import com.example.text_recognition.Class.Document;
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

public class HomeFragment extends Fragment {

    FloatingActionButton btn;
    ListView lvDocument;
    ArrayList<Document> arrayDocument ;
    DatabaseReference mData;
    DocumentAdapter adapter = null;
    public static final String URL_IMAGE = "URL_IMAGE";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_home, container, false);

        btn =linearLayout.findViewById(R.id.icAdd);
        lvDocument = linearLayout.findViewById(R.id.lvDocument);
        arrayDocument = new ArrayList<>();
        adapter = new DocumentAdapter(getActivity(), R.layout.document_row, arrayDocument);
        lvDocument.setAdapter(adapter);
        mData = FirebaseDatabase.getInstance().getReference();

        LoadData();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), OcrActivity.class);
                startActivity(intent);
            }
        });
        lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                String image = arrayDocument.get(position).getImage();
                intent.putExtra(URL_IMAGE, image);
                startActivity(intent);
            }
        });

        lvDocument.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String image = arrayDocument.get(position).getImage();
                String name = arrayDocument.get(position).getName();
                final Query query = mData.child("Document").orderByChild("image").equalTo(image);
                assert image != null;
                final StorageReference mImage = storage.getReferenceFromUrl(image);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
        return  linearLayout;
    }

    private void LoadData() {
        mData.child("Document").orderByChild("email").equalTo(user.getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Document document = dataSnapshot.getValue(Document.class);
                arrayDocument.add(new Document(document.getName(), document.getText(), document.getImage(),document.getEmail(),document.getEmailShare()));
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
    private void Delete(Query query, StorageReference mImage) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    ds.getRef().removeValue();
                }
                Toast.makeText(getActivity(),"Xoá file thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi không thể xoá", Toast.LENGTH_SHORT).show();
            }
        });

        mImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Xoá ảnh thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
