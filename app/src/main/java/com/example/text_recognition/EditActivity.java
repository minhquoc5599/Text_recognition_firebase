package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


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


public class EditActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    Button copy;
    Button importPDF;
    Button importTxt;
    Button shareText;
    Button shareImage;
    Toolbar toolbarOcr;

    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Connect();
        actionToolbar();

        mData = FirebaseDatabase.getInstance().getReference("Document");

        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.URL_IMAGE);

        Query query = mData.orderByChild("image").equalTo(name);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String text = "" + ds.child("text").getValue();
                    editText.setText(text);
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

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                copyToClipBoard();
            }
        });

        importPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageToPdf();
            }
        });

        importTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textToTxt();

            }
        });

        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareText();

            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
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
        editText = findViewById(R.id.result);
        imageView = findViewById(R.id.imageResult);
        copy = findViewById(R.id.btnCopy);
        importPDF = findViewById(R.id.importPDF);
        importTxt = findViewById(R.id.importTxt);
        shareText = findViewById(R.id.shareText);
        shareImage = findViewById(R.id.shareImage);
    }

    private void shareImage() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            File file = new File(EditActivity.this.getExternalCacheDir(), "Image.png");
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
            Toast.makeText(EditActivity.this, "File not found", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e) {
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
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(EditActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();

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

        File folder = new File(Environment.getExternalStorageDirectory(),"Text_recognition");

        if(!folder.exists())
        {
            boolean mkdir = folder.mkdir();
            if(!mkdir)
            {
                Toast.makeText(EditActivity.this, "Tạo thư mục thất bại!!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                File folder_2 = new File(folder, namePdf);
                File filePdf = new File(folder_2, namePdf + ".pdf");

                try
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(filePdf);
                    pdfDocument.writeTo(fileOutputStream);

                    Toast.makeText(EditActivity.this,"Image is saved to " + namePdf +".pdf", Toast.LENGTH_SHORT).show();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                pdfDocument.close();
            }
        }
        else
        {
            File folder_2 = new File(folder, namePdf);
            File filePdf = new File(folder_2, namePdf + ".pdf");

            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(filePdf);
                pdfDocument.writeTo(fileOutputStream);

                Toast.makeText(EditActivity.this,"Image is saved to " + namePdf +".pdf", Toast.LENGTH_SHORT).show();
            }catch (IOException e)
            {
                e.printStackTrace();
            }

            pdfDocument.close();
        }


    }

    private void textToTxt() {

        String text = editText.getText().toString();

        String nameTxt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        File folder = new File(Environment.getExternalStorageDirectory(),"Text_recognition");

        File newTxt = new File(folder,nameTxt + ".txt");

        FileOutputStream fout = null;


        try{
            fout = new FileOutputStream(newTxt);
            fout.write(text.getBytes());


            Toast.makeText(EditActivity.this, "Text is saved to " + nameTxt +".txt",Toast.LENGTH_SHORT).show();
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }finally {
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
