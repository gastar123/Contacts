package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

        final Contact contact = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(contact.getName());
        TextView tvNumber = convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(contact.getPhone());
        ImageButton call = convertView.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String call = "tel:" + contact.getPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(call));
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
