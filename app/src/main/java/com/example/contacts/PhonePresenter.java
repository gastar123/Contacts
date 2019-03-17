package com.example.contacts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class PhonePresenter {

    private MainActivity view;
    private final PhoneModel model;
    static final int CHANGE_CONTACT = 1;
    static final int ADD_CONTACT = 2;

    public PhonePresenter(PhoneModel model) {
        this.model = model;
    }

    public void setView(MainActivity view) {
        this.view = view;
    }

    public void add() {
        Intent intent = new Intent(view, PhoneActivity.class);
        intent.putExtra("requestCode", ADD_CONTACT);
        view.startActivityForResult(intent, ADD_CONTACT);
    }

    public void returnActivity(Contact contact, boolean update) {

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
}
