package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private List<Contact> phoneBook;

    public ContactAdapter(Context context, List<Contact> phonebook) {
        this.phoneBook = phonebook;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    String call = "tel:" + phoneBook.get(adapterPosition).getPhone();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(call));
                    context.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder viewHolder, int position) {
        Contact contact = phoneBook.get(position);
        viewHolder.tvName.setText(contact.getName());
        viewHolder.tvNumber.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return phoneBook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvNumber;
        final ImageView call;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvNumber = view.findViewById(R.id.tvNumber);
            call = view.findViewById(R.id.call);
        }
    }
}
