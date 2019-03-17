package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
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

//        rvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
//                startActivity(intent);
//                return false;
//            }
//        });

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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.add();
            }
        });

        adapter = new ContactAdapter(this, model.getPhoneBook());
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                presenter.edit(contact);
            }
        });
        rvMain.setAdapter(adapter);
    }

    public void updateView() {
        adapter.notifyDataSetChanged();
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
        if (data == null) return;
        Contact contact = (Contact) data.getSerializableExtra("contact");
        presenter.returnActivity(contact, requestCode);
        updateView();
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
}
