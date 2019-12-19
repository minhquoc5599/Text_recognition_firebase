package com.example.text_recognition.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.text_recognition.Class.Document;
import com.example.text_recognition.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends BaseAdapter {


    private Context context;
    private int layout;
    private List<Document> arrayDocument;

    public DocumentAdapter(Context context, int layout, List<Document> arrayDocument) {
        this.context = context;
        this.layout = layout;
        this.arrayDocument = arrayDocument;
    }

    @Override
    public int getCount() {
        return arrayDocument.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDocument.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder
    {
        TextView name;
        ImageView image;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        ViewHolder holder = new ViewHolder();
        if(rowView == null)
        {
            rowView = inflater.inflate(layout, null);
            holder.name = rowView.findViewById(R.id.name);
            holder.image = rowView.findViewById(R.id.imageDocument);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();

        }
        holder.name.setText(arrayDocument.get(position).getName());
        Picasso.get().load(arrayDocument.get(position).getImage()).into(holder.image);

        return rowView;
    }

}
