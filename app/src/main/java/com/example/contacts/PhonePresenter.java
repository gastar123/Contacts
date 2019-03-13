package com.example.contacts;

public class PhonePresenter {

    private MainActivity view;
    private final PhoneModel model;

    public PhonePresenter(PhoneModel model) {
        this.model = model;
    }

    public void setView(MainActivity view) {
        this.view = view;
    }
}
