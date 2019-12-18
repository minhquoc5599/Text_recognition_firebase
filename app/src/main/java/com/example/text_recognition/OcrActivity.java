package com.example.text_recognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class OcrActivity extends AppCompatActivity {

    EditText  nameDoc;
    ImageView img;
    Uri imgUri;
    Toolbar toolbarOcr;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mData;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        Connect();
        //actionToolbar();
        CropImage.activity().start(OcrActivity.this);

        final StorageReference storageRef = storage.getReference();
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        toolbarOcr.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.menuSave)
                {
                    String name = nameDoc.getText().toString();
                    if(name.isEmpty())
                    {
                        Toast.makeText(OcrActivity.this, "Bạn chưa đặt tên file", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Calendar calendar = Calendar.getInstance();
                        StorageReference mountainsRef = storageRef.child("image"+calendar.getTimeInMillis()+".png");
                        img.setDrawingCacheEnabled(true);
                        img.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = mountainsRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(OcrActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUri = uri.toString();
                                        Toast.makeText(OcrActivity.this, "Lưu hình thành công", Toast.LENGTH_SHORT).show();
                                        Log.d("AAA", downloadUri);

                                        // create node database
                                        String name = nameDoc.getText().toString();
                                        String text = "";
                                        assert user != null;
                                        String email = user.getEmail();
                                        String emailShare = "Thêm vào email cần chia sẻ";
                                        Document document = new Document(name, text, downloadUri, email, emailShare);
                                        mData.child("Document").push().setValue(document, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                if(databaseError == null)
                                                {
                                                    Toast.makeText(OcrActivity.this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new  Intent(OcrActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    Toast.makeText(OcrActivity.this, "Lỗi dữ liệu!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                });
                            }
                        });
                    }
                }
                return false;
            }
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarOcr);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbarOcr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Connect() {
        img = findViewById(R.id.imageResult);
        nameDoc = findViewById(R.id.nameDocument);
        toolbarOcr = findViewById(R.id.toolbarOcr);
        toolbarOcr.inflateMenu(R.menu.menu_save);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imgUri = result.getUri();

                img.setImageURI(imgUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
