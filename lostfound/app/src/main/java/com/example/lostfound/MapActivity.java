package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LostFoundDatabase db;
    private static final String TAG = "MAP_DEBUG";
    private final Map<Marker, Item> markerItemMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new LostFoundDatabase(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<Item> items = db.getAllItems();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boolean hasValidLocation = false;

        for (Item item : items) {
            Log.d(TAG, "Item: " + item.getTitle() + ", Location: " + item.getLocation());
            LatLng location = extractLatLng(item.getLocation());
            if (location != null) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(item.getTitle())
                        .snippet(item.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                "Lost".equalsIgnoreCase(item.getType()) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN
                        )));
                markerItemMap.put(marker, item);
                boundsBuilder.include(location);
                hasValidLocation = true;
            } else {
                Log.d(TAG, "Location parsing failed for: " + item.getLocation());
            }
        }

        if (hasValidLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.8688, 151.2093), 10));
        }

        mMap.setOnInfoWindowClickListener(marker -> {
            Item item = markerItemMap.get(marker);
            if (item != null) {
                Log.d(TAG, "Launching detail for item: " + item.getTitle());
                Intent intent = new Intent(MapActivity.this, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                intent.putExtra("item_title", item.getTitle());
                intent.putExtra("item_description", item.getDescription());
                intent.putExtra("item_location", item.getLocation());
                intent.putExtra("item_date", item.getDate());
                intent.putExtra("item_contact", item.getContact());
                intent.putExtra("item_type", item.getType());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No item found for this marker.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LatLng extractLatLng(String locationText) {
        try {
            if (locationText != null && locationText.contains("Lat:") && locationText.contains("Lng:")) {
                String[] parts = locationText.replace("Lat:", "").replace("Lng:", "").split(",");
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                return new LatLng(lat, lng);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse coordinates from: " + locationText, e);
        }
        return null;
    }
}
