package com.example.contacts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class PhoneModel {
    private final DBHelper dbHelper;
    private final ContentResolver contentResolver;
    private final List<Contact> phoneBook = new ArrayList<>();

    public PhoneModel(DBHelper dbHelper, ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.dbHelper = dbHelper;
    }

    public List<Contact> getPhoneBook() {
        return phoneBook;
    }

    /**
     * @return список контактов приложения
     */
    private void getContacts() {
        phoneBook.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.query("phones", null, null, null, null, null, "LOWER(name)");
        while (cur != null && cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String phone = cur.getString(cur.getColumnIndex("phone"));
            phoneBook.add(new Contact(id, name, phone));
        }
        cur.close();
        dbHelper.close();
    }

    public void getContacts(final Handler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();
                handler.sendEmptyMessage(1);
            }
        });
        t.start();
    }

    public void update(final Contact contact, final boolean update, final Handler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                cv.put("name", contact.getName());
                cv.put("phone", contact.getPhone());

                if(update) {
                    db.update("phones", cv, "id = " + contact.getId(), null);
                } else {
                    db.insert("phones", null, cv);
                }
                dbHelper.close();
                getContacts();
                handler.sendEmptyMessage(1);
            }
        });
        t.start();
    }

    /**
     * @return список контактов телефона
     */
    private List<Contact> getAndroidContacts() {
        List<Contact> phoneBook = new ArrayList<>();

        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur != null && cur.moveToNext()) {
            String id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            if (cur.getInt(cur.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = contentResolver.query(
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

    public void syncPhoneBook(final Handler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
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
                handler.sendEmptyMessage(1);
            }
        });
        t.start();
    }

    public void deletePhoneBook(final Handler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                db.delete("phones", null, null);
                dbHelper.close();
                getContacts();
                handler.sendEmptyMessage(1);
            }
        });
        t.start();
    }
}
