package com.example.contacts;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact> phoneBook;
    private ListView lvMain;
    private int position;
    private ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        lvMain = findViewById(R.id.lvMain);

        phoneBook = createContacts();

        adapter = new ContactAdapter(this, R.layout.contact, phoneBook);
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.position = position;
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                intent.putExtra("contact", phoneBook.get(position));
                startActivityForResult(intent, 1);
            }
        });


////        LinearLayout linLayout = findViewById(R.id.linLayout);
////
////        LayoutInflater lnInflater = getLayoutInflater();
//
//        for (Contact c : phoneBook) {
//            View item = lnInflater.inflate(R.layout.contact, linLayout, false);
//            TextView tvName = item.findViewById(R.id.tvName);
//            tvName.setText(c.getName());
//            TextView tvNumber = item.findViewById(R.id.tvNumber);
//            tvNumber.setText(c.getPhone());
//            item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
//            linLayout.addView(item);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        Contact contact = (Contact) data.getSerializableExtra("contact");
        phoneBook.remove(position);
        phoneBook.add(position, contact);
        adapter.notifyDataSetChanged();
    }

    private List<Contact> createContacts() {
        List<Contact> phoneBook = new ArrayList<>();
        phoneBook.add(new Contact("Иван", "1234"));
        phoneBook.add(new Contact("Илья", "4321"));
        phoneBook.add(new Contact("Антон", "3421"));
        return phoneBook;
    }
}
