package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editLocation, editDate, editContact;
    private RadioGroup radioGroupType;
    private Button btnSubmit;
    private LostFoundDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        database = new LostFoundDatabase(this);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        editDate = findViewById(R.id.editDate);
        editContact = findViewById(R.id.editContact);
        radioGroupType = findViewById(R.id.radioGroupType);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String location = editLocation.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String contact = editContact.getText().toString().trim();
            String type = (radioGroupType.getCheckedRadioButtonId() == R.id.radioLost) ? "Lost" : "Found";

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                    TextUtils.isEmpty(location) || TextUtils.isEmpty(date) || TextUtils.isEmpty(contact)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Item item = new Item(title, description, location, date, contact, type);
            database.insertItem(item);
            Toast.makeText(this, "Item submitted successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AddItemActivity.this, ItemListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}