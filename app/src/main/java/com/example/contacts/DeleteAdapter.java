package com.example.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.ViewHolder> {
    private Context context;
    private final List<Contact> phoneBook = new ArrayList<>();
    private final HashSet<Integer> idList = new HashSet<>();

    public HashSet<Integer> getIdList() {
        return idList;
    }

    public DeleteAdapter(Context context) {
        this.context = context;
    }

    public void changeData(List<Contact> phoneBook) {
        this.phoneBook.clear();
        this.phoneBook.addAll(phoneBook);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeleteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ondelete_contact, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cbContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (isChecked) {
                    idList.add(phoneBook.get(adapterPosition).getId());
                } else if (!isChecked) {
                    idList.remove(phoneBook.get(adapterPosition).getId());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteAdapter.ViewHolder viewHolder, int position) {
        Contact contact = phoneBook.get(position);
        viewHolder.tvName.setText(contact.getName());
        viewHolder.tvNumber.setText(contact.getPhone());
        if (idList.contains(phoneBook.get(position).getId())) {
            viewHolder.cbContact.setChecked(true);
        } else {
            viewHolder.cbContact.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return phoneBook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvNumber;
        final CheckBox cbContact;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvNumber = view.findViewById(R.id.tvNumber);
            cbContact = view.findViewById(R.id.cbContact);
        }
    }
}