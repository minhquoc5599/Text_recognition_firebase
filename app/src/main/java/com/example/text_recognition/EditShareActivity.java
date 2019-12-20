package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditShareActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    TextView emailUser, copy, exportPDF, exportTxt, share, update, delete, ocr;
    Toolbar toolbarEditShare;
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    int REQUEST_CODE_READ_WRITE_STORAGE_PDF = 123;
    int REQUEST_CODE_READ_WRITE_STORAGE_TEXT = 133;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_share);

        Connect();
        actionToolbar();
        Intent intent = getIntent();
        String image = intent.getStringExtra(HomeFragment.URL_IMAGE);
        final Query query = mData.child("Document").orderByChild("image").equalTo(image);
        LoadData(query);


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard();
            }
        });

        exportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(EditShareActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_WRITE_STORAGE_PDF);
            }
        });

        exportTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(EditShareActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_WRITE_STORAGE_TEXT);
                textToTxt();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update(query);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(query);
            }
        });

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ocr();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE_READ_WRITE_STORAGE_PDF && grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            imageToPdf();
        }
        else if(requestCode==REQUEST_CODE_READ_WRITE_STORAGE_TEXT && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            textToTxt();
        }
        else
        {
            Toast.makeText(EditShareActivity.this,"Bạn không thể ghi file", Toast.LENGTH_SHORT).show();
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void Ocr() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

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
            editText.setText(stringBuilder.toString());
        }
    }
    private void showShare() {
        String [] items ={"Share Text", "Share Image"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Share");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    shareText();
                }
                if(which == 1)
                {
                    shareImage();
                }
            }
        });
        dialog.create().show();
    }

    private void Update(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String text = editText.getText().toString();
                    ds.getRef().child("text").setValue(text);
                }
                Toast.makeText(EditShareActivity.this,"Đã lưu", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditShareActivity.this, "Lỗi không thể lưu", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Delete(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String user ="Thêm vào email cần chia sẻ";
                    ds.getRef().child("emailShare").setValue(user);
                }
                Toast.makeText(EditShareActivity.this,"Xoá file thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditShareActivity.this, "Lỗi không thể xoá", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(EditShareActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void LoadData(Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String text = "" + ds.child("text").getValue();
                    editText.setText(text);
                    String email = "" + ds.child("email").getValue();
                    emailUser.setText(email);
                    String image = "" + ds.child("image").getValue();
                    try{
                        Picasso.get().load(image).into(imageView);
                    }catch(Exception e)
                    {
                        Picasso.get().load(R.drawable.error).into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditShareActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarEditShare);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarEditShare.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Connect() {

        toolbarEditShare = findViewById(R.id.toolbarEditShare);
        editText = findViewById(R.id.resultShare);
        imageView = findViewById(R.id.imageEditShare);
        emailUser = findViewById(R.id.emailUserShare);
        copy = findViewById(R.id.btnCopy);
        exportPDF = findViewById(R.id.exportPDF);
        exportTxt = findViewById(R.id.exportTxt);
        share = findViewById(R.id.share);
        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);
        ocr = findViewById(R.id.btnOcr);
    }

    private void shareImage() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            File file = new File(EditShareActivity.this.getExternalCacheDir(), "Image.png");
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fout);
            fout.flush();
            fout.close();
            file.setReadable(true, false);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Select how you want to share the text"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(EditShareActivity.this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void shareText() {
        String text = editText.getText().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Write your subject here");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Select how you want to share the text") );
    }

    private void copyToClipBoard() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("EditText", editText.getText().toString());
        if(clipboardManager!=null)
        {
            clipboardManager.setPrimaryClip(clip);
        }
        Toast.makeText(EditShareActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();

    }
    private void imageToPdf() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pi);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(), bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);

        canvas.drawBitmap(bitmap,0,0,null);

        pdfDocument.finishPage(page);


        // save bitmap image
        String namePdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        File folder = new File(Environment.getExternalStorageDirectory(), "Text_recognition");

        boolean mkdir = true;

        if(!folder.exists())
        {
            mkdir = folder.mkdir();

        }
        if(!mkdir)
        {
            Toast.makeText(EditShareActivity.this, "Tạo thư mục thất bại!!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            File filePdf = new File(folder, namePdf + ".pdf");
            FileOutputStream fileOutputStream = null;
            try
            {
                fileOutputStream = new FileOutputStream(filePdf);
                pdfDocument.writeTo(fileOutputStream);
                Toast.makeText(EditShareActivity.this,"Image is saved to " + folder+ "/"+ namePdf +".pdf", Toast.LENGTH_SHORT).show();
            }catch (IOException e)
            {
                e.printStackTrace();
            }finally {
                if(fileOutputStream!= null)
                {
                    try{
                        fileOutputStream.close();
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                }

            }
            pdfDocument.close();
        }


    }

    private void textToTxt() {

        String text = editText.getText().toString();

        String nameTxt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        File folder = new File(Environment.getExternalStorageDirectory(),"Text_recognition");

        boolean mkdir = true;

        if(!folder.exists())
        {
            mkdir = folder.mkdir();
        }
        if(!mkdir)
        {
            Toast.makeText(EditShareActivity.this, "Tạo thư mục thất bại!!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            File newTxt = new File(folder,nameTxt + ".txt");
            FileOutputStream fout = null;
            try{
                fout = new FileOutputStream(newTxt);
                fout.write(text.getBytes());
                Toast.makeText(EditShareActivity.this, "Text is saved to " +folder +"/" + nameTxt +".txt",Toast.LENGTH_SHORT).show();
            } catch (IOException e)
            {
                e.printStackTrace();
            } finally {
                if(fout!= null)
                {
                    try{
                        fout.close();
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

}

