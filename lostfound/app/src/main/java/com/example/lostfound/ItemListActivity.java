package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner spinnerTypeFilter;
    private ItemAdapter adapter;
    private LostFoundDatabase database;
    private List<Item> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        database = new LostFoundDatabase(this);
        recyclerView = findViewById(R.id.recyclerViewItems);
        spinnerTypeFilter = findViewById(R.id.spinnerTypeFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allItems = database.getAllItems();
        Log.d("ItemListActivity", "Items loaded: " + allItems.size());

        adapter = new ItemAdapter(allItems, item -> {
            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, new String[]{"All", "Lost", "Found"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeFilter.setAdapter(spinnerAdapter);

        spinnerTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                filterItems(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterItems("All");
            }
        });
    }

    private void filterItems(String type) {
        if (adapter == null || allItems == null) return;

        if (type.equals("All")) {
            adapter.updateList(allItems);
        } else {
            List<Item> filtered = new ArrayList<>();
            for (Item item : allItems) {
                if (item.getType().equalsIgnoreCase(type)) {
                    filtered.add(item);
                }
            }
            adapter.updateList(filtered);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allItems = database.getAllItems();
        filterItems(spinnerTypeFilter.getSelectedItem().toString());
    }
}
