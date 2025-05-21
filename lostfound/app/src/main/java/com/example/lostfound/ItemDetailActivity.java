package com.example.lostfound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textTitle, textDescription, textLocation, textDate, textContact, textType;
    private Button btnRemove;
    private LostFoundDatabase database;
    private String location;
    private int itemId = -1;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize DB
        database = new LostFoundDatabase(this);

        // Bind views
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textLocation = findViewById(R.id.textLocation);
        textDate = findViewById(R.id.textDate);
        textContact = findViewById(R.id.textContact);
        textType = findViewById(R.id.textType);
        btnRemove = findViewById(R.id.btnRemove);

        // Get item ID from intent
        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId != -1) {
            Item item = database.getItemById(itemId);

            if (item != null) {
                location = item.getLocation();
                textTitle.setText("Title: " + item.getTitle());
                textDescription.setText("Description: " + item.getDescription());
                textLocation.setText("Location: " + location);
                textDate.setText("Date: " + item.getDate());
                textContact.setText("Contact: " + item.getContact());
                textType.setText("Type: " + item.getType());
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "No item ID passed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailMapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Remove button
        btnRemove.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to remove this advert?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        database.deleteItem(itemId);
                        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();

                        // Optionally return to MapActivity
                        Intent intent = new Intent(this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = extractLatLng(location);
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private LatLng extractLatLng(String locationText) {
        try {
            if (locationText != null && locationText.contains("Lat:") && locationText.contains("Lng:")) {
                String[] parts = locationText.replace("Lat:", "").replace("Lng:", "").split(",");
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                return new LatLng(lat, lng);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
