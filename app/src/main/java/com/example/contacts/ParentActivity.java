package com.example.contacts;

import android.support.v7.app.AppCompatActivity;

import java.util.List;

public abstract class ParentActivity extends AppCompatActivity {
    public abstract void updateView(List<Contact> phoneBook);

    public abstract int getScrollPosition();

    public abstract int getScrollOffset();
}
