package com.example.contacts;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class DeleteActivity extends ParentActivity {
    private PhonePresenter presenter;
    private RecyclerView rvDelete;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_contacts);
        init();
    }

    private void init() {
        DBHelper dbHelper = new DBHelper(this);
        ContentResolver contentResolver = getContentResolver();
        PhoneModel model = new PhoneModel(dbHelper, contentResolver);
        presenter = new PhonePresenter(model);
        presenter.setView(this);

        rvDelete = findViewById(R.id.rvDelete);
        rvDelete.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ContactAdapter(this);
        rvDelete.setAdapter(adapter);
        presenter.loadAll();
    }

    @Override
    public void updateView(List<Contact> phoneBook) {
        adapter.changeData(phoneBook);
    }
}
