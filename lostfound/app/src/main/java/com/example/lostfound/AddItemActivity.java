package com.example.lostfound;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editLocation, editDate, editContact;
    private RadioGroup radioGroupType;
    private Button btnSubmit;
    private LostFoundDatabase database;
    private LatLng selectedLatLng;

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

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        // Autocomplete
        editLocation.setFocusable(false);
        editLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, 100);
        });

        // Get current location
        findViewById(R.id.btnCurrentLocation).setOnClickListener(v -> {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    editLocation.setText("Lat: " + selectedLatLng.latitude + ", Lng: " + selectedLatLng.longitude);
                }
            });
        });

        // Show all on map
        findViewById(R.id.btnLocationMap).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        // Submit
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            selectedLatLng = place.getLatLng();

            if (selectedLatLng != null) {
                editLocation.setText("Lat: " + selectedLatLng.latitude + ", Lng: " + selectedLatLng.longitude);
            } else {
                editLocation.setText(place.getAddress());
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
