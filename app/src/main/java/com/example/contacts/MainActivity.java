package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PhonePresenter presenter;

    private ListView lvMain;
    private ArrayAdapter<Contact> adapter;
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
//        getContacts();
        }

        lvMain = findViewById(R.id.lvMain);

        adapter = new ContactAdapter(this, R.layout.contact, phoneBook);
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                intent.putExtra("requestCode", CHANGE_CONTACT);
                intent.putExtra("contact", phoneBook.get(position));
                startActivityForResult(intent, CHANGE_CONTACT);
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
                startActivity(intent);
                return false;
            }
        });

        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.mytrans);
            Animation anim_two = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_two);
            ImageView imageView = findViewById(R.id.imageView);

            private int prevFirstItem;
            private boolean start = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (prevFirstItem < firstVisibleItem && start) {
                    imageView.startAnimation(anim);
                    start = false;
                }

                if (prevFirstItem > firstVisibleItem && !start) {
                    imageView.startAnimation(anim_two);
                    start = true;
                }

                prevFirstItem = firstVisibleItem;
            }
        });
    }

    private void init() {
        DBHelper dbHelper = new DBHelper(this);
        ContentResolver contentResolver = getContentResolver();
        PhoneModel model = new PhoneModel(dbHelper, contentResolver);
        presenter = new PhonePresenter(model);
        presenter.setView(this);
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
//            getContacts();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    public void addContact(View v) {
        Intent intent = new Intent(this, PhoneActivity.class);
        intent.putExtra("requestCode", ADD_CONTACT);
        startActivityForResult(intent, ADD_CONTACT);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Contact contact = (Contact) data.getSerializableExtra("contact");
        switch (requestCode) {
            case CHANGE_CONTACT:
                cv.put("name", contact.getName());
                cv.put("phone", contact.getPhone());
                db.update("phones", cv, "id = " + contact.getId(), null);
                break;
            case ADD_CONTACT:
                cv.put("name", contact.getName());
                cv.put("phone", contact.getPhone());
                db.insert("phones", null, cv);
                break;
        }
        dbHelper.close();
        getContacts();
        adapter.notifyDataSetChanged();
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
            syncPhoneBook();
        } else if (item.getItemId() == 2) {
            deletePhoneBook();
        }
        return super.onOptionsItemSelected(item);
    }

    private void syncPhoneBook() {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for (Contact c : getAndroidContacts()) {
            try {
                cv.put("name", c.getName());
                cv.put("phone", c.getPhone().replaceAll("[ \\-()]", ""));
                db.insert("phones", null, cv);
            } catch (SQLiteConstraintException e) {
            }
        }
        dbHelper.close();
        getContacts();
        adapter.notifyDataSetChanged();
    }

    private void deletePhoneBook() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("phones", null, null);
        db.close();
        getContacts();
        adapter.notifyDataSetChanged();
    }
}
