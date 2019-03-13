package com.example.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class DeleteActivity extends AppCompatActivity {

    private ListView lvDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_contacts);

        lvDelete = findViewById(R.id.lvDelete);
    }
}
