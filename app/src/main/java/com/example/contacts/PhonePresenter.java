package com.example.contacts;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class PhonePresenter {
    private Handler handler;
    private MainActivity view;
    private final PhoneModel model;
    static final int CHANGE_CONTACT = 1;
    static final int ADD_CONTACT = 2;

    public PhonePresenter(PhoneModel model) {
        this.model = model;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                view.updateView();
            }
        };
    }

    public void setView(MainActivity view) {
        this.view = view;
    }

    public void add() {
        Intent intent = new Intent(view, PhoneActivity.class);
        intent.putExtra("requestCode", ADD_CONTACT);
        view.startActivityForResult(intent, ADD_CONTACT);
    }

    public void edit(Contact contact) {
        Intent intent = new Intent(view, PhoneActivity.class);
        intent.putExtra("requestCode", PhonePresenter.CHANGE_CONTACT);
        intent.putExtra("contact", contact);
        view.startActivityForResult(intent, PhonePresenter.CHANGE_CONTACT);
    }

    public void returnActivity(Contact contact, int requestCode) {
        if (requestCode == ADD_CONTACT) {
            model.update(contact, false, handler);
        } else if (requestCode == CHANGE_CONTACT) {
            model.update(contact, true, handler);
        }
    }

    public void loadAll() {
        model.getContacts(handler);
    }

    public void syncPhoneBook() {
        model.syncPhoneBook(handler);
    }

    public void deletePhoneBook() {
        model.deletePhoneBook(handler);
    }
}
