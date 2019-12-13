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
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class OcrActivity extends AppCompatActivity {

    EditText mResult;

    ImageView img;

    Uri imgUri;


    Button copy;

    Button importPDF;

    Button importTxt;

    Button shareText;

    Button shareImage;


//    public static final String TEXT_OCR ="TEXTOCR";
//    public static final String TEXT_NAME ="TEXTNAME";
//    public static final String IMAGE_OCR ="IMAGEOCR";
//    public static final String BUNDLE ="BUNDLE";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        mResult = findViewById(R.id.result);

        img = findViewById(R.id.imageResult);

        copy = findViewById(R.id.btnCopy);

        importPDF = findViewById(R.id.importPDF);

        importTxt = findViewById(R.id.importTxt);

        shareText = findViewById(R.id.shareText);

        shareImage = findViewById(R.id.shareImage);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        CropImage.activity().start(OcrActivity.this);



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

    private void shareImage() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            File file = new File(OcrActivity.this.getExternalCacheDir(), "Image.png");
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
            Toast.makeText(OcrActivity.this, "File not found", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void shareText() {
        String text = mResult.getText().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Write your subject here");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Select how you want to share the text") );
    }


    private void copyToClipBoard() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("EditText", mResult.getText().toString());
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(OcrActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();

    }
    private void imageToPdf() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
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

        File folder_2 = new File(folder, namePdf);

        if(!folder.exists() || !folder_2.exists())
        {
            folder.mkdir();
            folder_2.mkdir();
        }

        File filePdf = new File(folder_2, namePdf + ".pdf");

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(filePdf);
            pdfDocument.writeTo(fileOutputStream);

            Toast.makeText(OcrActivity.this,"Image is saved to " + namePdf +".pdf", Toast.LENGTH_SHORT).show();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        pdfDocument.close();

    }

    private void textToTxt() {

        String text = mResult.getText().toString();

        String nameTxt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        File folder = new File(Environment.getExternalStorageDirectory(),"Text_recognition");

        File newTxt = new File(folder,nameTxt + ".txt");

        FileOutputStream fout = null;


        try{
            fout = new FileOutputStream(newTxt);
            fout.write(text.getBytes());


            Toast.makeText(OcrActivity.this, "Text is saved to " + nameTxt +".txt",Toast.LENGTH_SHORT).show();
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


    /*private void byBundle() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, bs);
        byte[] b = bs.toByteArray();

        Intent intent = new Intent(OcrActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_OCR, mResult.getText().toString());
        bundle.putByteArray(IMAGE_OCR, b);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
    }*/

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
