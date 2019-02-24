package com.example.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Contact> phoneBook = createContacts();

        LinearLayout linLayout = findViewById(R.id.linLayout);

        LayoutInflater lnInflater = getLayoutInflater();

        for (Contact c : phoneBook) {
            View item = lnInflater.inflate(R.layout.contact, linLayout, false);
            TextView tvName = item.findViewById(R.id.tvName);
            tvName.setText(c.getName());
            TextView tvNumber = item.findViewById(R.id.tvNumber);
            tvNumber.setText(c.getPhone());
            item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            linLayout.addView(item);
        }
    }

    private List<Contact> createContacts() {
        List<Contact> phoneBook = new ArrayList<>();
        phoneBook.add(new Contact("Иван", "1234"));
        phoneBook.add(new Contact("Илья", "4321"));
        phoneBook.add(new Contact("Антон", "3421"));
        return phoneBook;
    }
}
