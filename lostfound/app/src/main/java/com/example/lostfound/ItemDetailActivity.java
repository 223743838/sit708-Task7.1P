package com.example.lostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView textDetailTitle, textDetailDescription, textDetailLocation, textDetailDate, textDetailContact, textDetailType;
    private Button btnRemove;
    private LostFoundDatabase database;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        database = new LostFoundDatabase(this);

        textDetailTitle = findViewById(R.id.textDetailTitle);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textDetailLocation = findViewById(R.id.textDetailLocation);
        textDetailDate = findViewById(R.id.textDetailDate);
        textDetailContact = findViewById(R.id.textDetailContact);
        textDetailType = findViewById(R.id.textDetailType);
        btnRemove = findViewById(R.id.btnRemove);

        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId != -1) {
            Item item = database.getItemById(itemId);
            if (item != null) {
                textDetailTitle.setText("Title: " + item.getTitle());
                textDetailDescription.setText("Description: " + item.getDescription());
                textDetailLocation.setText("Location: " + item.getLocation());
                textDetailDate.setText("Date: " + item.getDate());
                textDetailContact.setText("Contact: " + item.getContact());
                textDetailType.setText("Type: " + item.getType());
            }
        }

        btnRemove.setOnClickListener(v -> {
            if (itemId != -1) {
                database.deleteItem(itemId);
                Toast.makeText(ItemDetailActivity.this, "Advert removed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}