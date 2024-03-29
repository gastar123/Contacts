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

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private final List<Contact> phoneBook = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnItemClickListener onItemLongClickListener;

    public ContactAdapter(Context context) {
        this.context = context;
    }

    public void changeData(List<Contact> phoneBook) {
        this.phoneBook.clear();
        this.phoneBook.addAll(phoneBook);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.call.setOnClickListener(v -> {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String call = "tel:" + phoneBook.get(adapterPosition).getPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(call));
                context.startActivity(intent);
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = viewHolder.getAdapterPosition();
                onItemClickListener.onItemClick(phoneBook.get(adapterPosition));
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = viewHolder.getAdapterPosition();
                onItemLongClickListener.onItemClick(phoneBook.get(adapterPosition));
                return false;
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

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
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
