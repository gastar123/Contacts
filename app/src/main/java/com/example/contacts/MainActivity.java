package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Contact> phoneBook = new ArrayList<>();
    private ListView lvMain;
    private int position;
    private ArrayAdapter<Contact> adapter;
    private DBHelper dbHelper;
    private final int CHANGE_CONTACT = 1;
    private final int ADD_CONTACT = 2;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        dbHelper = new DBHelper(this);

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
            getContacts();
        }

        lvMain = findViewById(R.id.lvMain);

        adapter = new ContactAdapter(this, R.layout.contact, phoneBook);
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.position = position;
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                intent.putExtra("requestCode", CHANGE_CONTACT);
                intent.putExtra("contact", phoneBook.get(position));
                startActivityForResult(intent, CHANGE_CONTACT);
            }
        });
    }

    public void addContact(View v) {
        Intent intent = new Intent(this, PhoneActivity.class);
        intent.putExtra("requestCode", ADD_CONTACT);
        startActivityForResult(intent, ADD_CONTACT);
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
            getContacts();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
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
//                phoneBook.remove(position);
//                phoneBook.add(position, contact);
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

    /**
     *
     * @return список контактов приложения
     */
    private void getContacts() {
        phoneBook.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.query("phones", null, null, null, null, null, null);
        while (cur != null && cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String phone = cur.getString(cur.getColumnIndex("phone"));
            phoneBook.add(new Contact(id, name, phone));
        }
        cur.close();
        dbHelper.close();
    }

    /**
     *
     * @return список контактов телефона
     */
    private List<Contact> getAndroidContacts() {
        List<Contact> phoneBook = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        while (cur != null && cur.moveToNext()) {
            String id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            if (cur.getInt(cur.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneBook.add(new Contact(name, phoneNo));
                }
                pCur.close();
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneBook;
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

        for (Contact c: getAndroidContacts()) {
            cv.put("name", c.getName());
            cv.put("phone", c.getPhone());
            db.insert("phones",null, cv);
        }
        dbHelper.close();
        getContacts();
        adapter.notifyDataSetChanged();
    }

    private void deletePhoneBook() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("phones",null,null);
        db.close();
        getContacts();
        adapter.notifyDataSetChanged();
    }
}
