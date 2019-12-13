package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class EditActivity extends AppCompatActivity {

    EditText editText;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editText =findViewById(R.id.editText);

        imageView =findViewById(R.id.imageEdit);


//        setDataByBundle();

    }

    /*private void setDataByBundle() {

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(OcrActivity.BUNDLE);

        String textOcr = bundle.getString(OcrActivity.TEXT_OCR);
        editText.setText(textOcr);

        byte[] b = bundle.getByteArray(OcrActivity.IMAGE_OCR);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);


        //Bitmap bitmap =intent.getParcelableExtra(OcrActivity.IMAGEOCR);

        imageView.setImageBitmap(bitmap);

    }*/

}
