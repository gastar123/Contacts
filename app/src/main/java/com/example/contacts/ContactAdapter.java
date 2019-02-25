package com.example.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact, null);
        }

        Contact contact = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(contact.getName());
        TextView tvNumber = convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(contact.getPhone());
//        convertView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        return convertView;
    }
}
