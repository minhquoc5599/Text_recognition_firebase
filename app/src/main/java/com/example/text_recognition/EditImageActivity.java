package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.text_recognition.Interface.BrushFragmentListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditImageActivity extends AppCompatActivity implements BrushFragmentListener {

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    Toolbar toolbarEditImage;
    CardView btn_brush;
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        Intent intent = getIntent();
        final String image = intent.getStringExtra(EditActivity.URL_EDIT_IMAGE);
        Connect();
        assert image != null;
        final Query query = mData.child("Document").orderByChild("image").equalTo(image);
        final StorageReference mImage = storage.getReferenceFromUrl(image);

        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .build();

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.setBrushDrawingMode(true);
                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(EditImageActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

            }
        });

        Picasso.get().load(image).into(photoEditorView.getSource());
        final StorageReference storageRef = storage.getReference();

        toolbarEditImage.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.menuSaveEdit)
                {
                    photoEditor.saveAsBitmap(new OnSaveBitmap() {
                        @Override
                        public void onBitmapReady(Bitmap saveBitmap) {
                            Calendar calendar = Calendar.getInstance();
                            StorageReference mountainsRef = storageRef.child("image"+calendar.getTimeInMillis()+".png");
                            photoEditorView.getSource().setImageBitmap(saveBitmap);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = mountainsRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(EditImageActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downloadUri = uri.toString();
                                            Toast.makeText(EditImageActivity.this, "Lưu hình thành công", Toast.LENGTH_SHORT).show();
                                            Log.d("AAA", downloadUri);

                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ds: dataSnapshot.getChildren())
                                                    {
                                                        ds.getRef().child("image").setValue(downloadUri);
                                                    }
                                                    Toast.makeText(EditImageActivity.this,"Đã lưu", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(EditImageActivity.this, "Lỗi không thể lưu", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            mImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(EditImageActivity.this, "Xoá ảnh cũ thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditImageActivity.this, "Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    Intent intent = new Intent(EditImageActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void Connect() {

        photoEditorView = findViewById(R.id.image_preview);
        btn_brush = findViewById(R.id.btn_brush);
        toolbarEditImage = findViewById(R.id.toolbarEditImage);
        toolbarEditImage.inflateMenu(R.menu.menu_save_edit);
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarEditImage);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarEditImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if(isEraser)
        {
            photoEditor.brushEraser();
        }
        else
        {
            photoEditor.setBrushDrawingMode(true);
        }
    }
}
