package com.example.lostandfoundappbygeo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DBHelper dbHelper;
    private TextView tvNoItems;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        tvNoItems = findViewById(R.id.tvNoItems);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<Item> itemList = dbHelper.getAllItems();

        if (itemList.isEmpty()) {
            tvNoItems.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            tvNoItems.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);

            if (itemAdapter == null) {
                itemAdapter = new ItemAdapter(itemList, this);
                recyclerView.setAdapter(itemAdapter);
            } else {
                itemAdapter.updateData(itemList);
            }
        }
    }
}