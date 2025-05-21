package com.example.lostandfoundappbygeo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView tvItemType, tvItemName, tvItemPhone,
            tvItemDescription, tvItemDate, tvItemLocation;
    private Button btnRemove;
    private ImageButton btnBack;
    private DBHelper dbHelper;
    private int itemId;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DBHelper(this);

        tvItemType = findViewById(R.id.tvItemType);
        tvItemName = findViewById(R.id.tvItemName);
        tvItemPhone = findViewById(R.id.tvItemPhone);
        tvItemDescription = findViewById(R.id.tvItemDescription);
        tvItemDate = findViewById(R.id.tvItemDate);
        tvItemLocation = findViewById(R.id.tvItemLocation);
        btnRemove = findViewById(R.id.btnRemove);
        btnBack = findViewById(R.id.btnBack);


        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId == -1) {
            Toast.makeText(this, "Error loading item details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetails();

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadItemDetails() {
        currentItem = dbHelper.getItem(itemId);

        if (currentItem != null) {
            tvItemType.setText(currentItem.getType() + " "+ currentItem.getName());
            tvItemName.setText(currentItem.getName());
            tvItemPhone.setText(currentItem.getPhone());
            tvItemDescription.setText(currentItem.getDescription());
            tvItemDate.setText(currentItem.getDate());
            tvItemLocation.setText(currentItem.getLocation());
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void removeItem() {
        dbHelper.deleteItem(itemId);
        Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}