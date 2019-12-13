package com.example.text_recognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DocumentAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Document> documentList;

    public DocumentAdapter(Context context, int layout, List<Document> documentList) {
        this.context = context;
        this.layout = layout;
        this.documentList = documentList;
    }

    @Override
    public int getCount() {
        return documentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(layout,null);

        ImageView imageView = convertView.findViewById(R.id.imageDocument);

        TextView textView =convertView.findViewById(R.id.nameDocument);

        Document document =documentList.get(position);

        textView.setText(document.getName());

        imageView.setImageResource(document.getImage());



        return  convertView;
    }
}
