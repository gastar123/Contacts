package com.example.contacts;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class DeleteActivity extends ParentActivity {
    private PhonePresenter presenter;
    private RecyclerView rvDelete;
    private DeleteAdapter adapter;
    private Button btnDelete;

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
        adapter = new DeleteAdapter(this);
        rvDelete.setAdapter(adapter);
        presenter.loadAll();

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> presenter.onDeleteContact(adapter.getIdList()));
    }

    @Override
    public void updateView(List<Contact> phoneBook) {
        adapter.changeData(phoneBook);
        int scrollPosition = getIntent().getExtras().getInt("scrollPosition");
        int scrollOffset = getIntent().getExtras().getInt("scrollOffset");
        ((LinearLayoutManager) rvDelete.getLayoutManager()).scrollToPositionWithOffset(scrollPosition, scrollOffset);
    }

    @Override
    public int getScrollPosition() {
        return ((LinearLayoutManager) rvDelete.getLayoutManager()).findFirstVisibleItemPosition();
    }

    @Override
    public int getScrollOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvDelete.getLayoutManager();
        View v = layoutManager.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - layoutManager.getPaddingTop());
        return top;
    }
}
