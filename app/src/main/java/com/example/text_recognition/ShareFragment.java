package com.example.text_recognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ShareFragment extends Fragment {

    ListView lvShare;
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_share, container, false);

        lvShare = linearLayout.findViewById(R.id.lvShare);
        arrayDocument = new ArrayList<>();
        adapter = new DocumentAdapter(getActivity(), R.layout.document_row, arrayDocument);
        lvShare.setAdapter(adapter);
        mData = FirebaseDatabase.getInstance().getReference();

        LoadData();

        lvShare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                String image = arrayDocument.get(position).getImage();
                intent.putExtra(URL_IMAGE, image);
                startActivity(intent);
            }
        });


        return  linearLayout;
    }

    private void LoadData() {
        mData.child("Document").orderByChild("emailShare").equalTo(user.getEmail()).addChildEventListener(new ChildEventListener() {
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
}
