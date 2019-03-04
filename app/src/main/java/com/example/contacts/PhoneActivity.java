package com.example.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnEnter;
    private EditText etName;
    private EditText etPhone;
    private Contact contact;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        contact = (Contact) getIntent().getSerializableExtra("contact");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnEnter = findViewById(R.id.btnEnter);

        requestCode = getIntent().getExtras().getInt("requestCode");
        if (requestCode == MainActivity.CHANGE_CONTACT) {
            etName.setText(contact.getName());
            etPhone.setText(contact.getPhone());
        } else if (requestCode == MainActivity.ADD_CONTACT) {
            contact = new Contact("", "");
        }

        btnEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean error = false;
        if (etName.getText().toString().equals("")) {
            etName.setError("Empty name!");
            error = true;
        }
        if (etPhone.getText().toString().equals("")) {
            etPhone.setError("Empty phone!");
            error = true;
        }
        if (error) {
            return;
        }
        Intent intent = getIntent();
        contact.setName(etName.getText().toString());
        contact.setPhone(etPhone.getText().toString());
        intent.putExtra("contact", contact);
        setResult(RESULT_OK, intent);
        finish();
    }
}
