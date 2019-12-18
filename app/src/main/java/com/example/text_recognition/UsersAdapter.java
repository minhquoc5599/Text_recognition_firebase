package com.example.text_recognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class UsersAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Users> arrayUsers ;

    public UsersAdapter(Context context, int layout, List<Users> arrayUsers) {
        this.context = context;
        this.layout = layout;
        this.arrayUsers = arrayUsers;
    }


    @Override
    public int getCount() {
        return arrayUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder
    {
        TextView emailUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        UsersAdapter.ViewHolder holder = new UsersAdapter.ViewHolder();
        if(rowView == null)
        {
            rowView = inflater.inflate(layout, null);
            holder.emailUser = rowView.findViewById(R.id.emailUser);
            rowView.setTag(holder);
        }else {
            holder = (UsersAdapter.ViewHolder) rowView.getTag();

        }
        holder.emailUser.setText(arrayUsers.get(position).getEmail());

        return rowView;
    }
}
