package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.database.LostFoundDatabase;
import com.example.lostfound.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private LostFoundDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        database = new LostFoundDatabase(this);
        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();

        Button btnMap = findViewById(R.id.btnShowAllOnMap);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(ItemListActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Spinner spinner = findViewById(R.id.spinnerTypeFilter);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this, R.array.item_types, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                List<Item> items = database.getAllItems();
                if (!filter.equals("All")) {
                    List<Item> filtered = new ArrayList<>();
                    for (Item item : items) {
                        if (item.getType().equalsIgnoreCase(filter)) {
                            filtered.add(item);
                        }
                    }
                    adapter.updateList(filtered);
                } else {
                    adapter.updateList(items);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void loadItems() {
        List<Item> itemList = database.getAllItems();
        adapter = new ItemAdapter(itemList);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            intent.putExtra("item_id", item.getId());  // ✅ Pass only the ID
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems(); // Reload items when coming back
    }

}
