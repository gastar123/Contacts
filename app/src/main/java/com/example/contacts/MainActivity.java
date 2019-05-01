package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ParentActivity {
    private PhonePresenter presenter;
    private RecyclerView rvMain;
    private ImageView imageView;
    private ContactAdapter adapter;
    static final int CHANGE_CONTACT = 1;
    static final int ADD_CONTACT = 2;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        init();

// получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            presenter.loadAll();
        }

        rvMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.mytrans);
            Animation anim_two = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_two);
            private boolean start = true;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 1 && start) {
                    imageView.startAnimation(anim);
                    start = false;
                }

                if (dy < -1 && !start) {
                    imageView.startAnimation(anim_two);
                    start = true;
                }
            }
        });
    }

    private void init() {
        DBHelper dbHelper = new DBHelper(this);
        ContentResolver contentResolver = getContentResolver();
        PhoneModel model = new PhoneModel(dbHelper, contentResolver);
        presenter = new PhonePresenter(model);
        presenter.setView(this);

        rvMain = findViewById(R.id.rvMain);
        rvMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> presenter.add());

        adapter = new ContactAdapter(this);
        adapter.setOnItemClickListener(contact -> presenter.edit(contact));
        adapter.setOnItemLongClickListener(contact -> presenter.delete(contact));
        rvMain.setAdapter(adapter);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        presenter.loadAll();
//    }

    @Override
    public void updateView(List<Contact> phoneBook) {
        adapter.changeData(phoneBook);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    READ_CONTACTS_GRANTED = true;
                }
        }
        if (READ_CONTACTS_GRANTED) {
            presenter.loadAll();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 3) {
            presenter.loadAll();
        }
        if (data == null) return;
        Contact contact = (Contact) data.getSerializableExtra("contact");
        presenter.returnActivity(contact, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "synchronize");
        menu.add(0, 2, 0, "delete all");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            presenter.syncPhoneBook();
        } else if (item.getItemId() == 2) {
            presenter.deletePhoneBook();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getScrollPosition() {
        return ((LinearLayoutManager) rvMain.getLayoutManager()).findFirstVisibleItemPosition();
    }

    @Override
    public int getScrollOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvMain.getLayoutManager();
        View v = layoutManager.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - layoutManager.getPaddingTop());
        return top;
    }
}
