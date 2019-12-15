package com.example.text_recognition;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class OcrActivity extends AppCompatActivity {

    EditText mResult, nameDoc;
    ImageView img;
    Uri imgUri;
    Toolbar toolbarOcr;
    Button btnSave;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mData;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        Connect();
        actionToolbar();
        CropImage.activity().start(OcrActivity.this);

        final StorageReference storageRef = storage.getReference();
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameDoc.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(OcrActivity.this, "Bạn chưa đặt tên file", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar calendar =Calendar.getInstance();
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
                                    String text = mResult.getText().toString();
                                    assert user != null;
                                    String email = user.getEmail();
                                    Document document = new Document(name, text, downloadUri, email);
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
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarOcr);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbarOcr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Connect() {
        toolbarOcr = findViewById(R.id.toolbarOcr);
        mResult = findViewById(R.id.result);
        img = findViewById(R.id.imageResult);
        nameDoc = findViewById(R.id.nameDocument);
        btnSave = findViewById(R.id.btnSave);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();

                img.setImageURI(imgUri);


                BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();


                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("\n");

                    }

                    mResult.setText(stringBuilder.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
