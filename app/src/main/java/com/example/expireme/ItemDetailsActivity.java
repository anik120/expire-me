package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import utils.DatabaseHelper;
import utils.FoodItem;

public class ItemDetailsActivity extends AppCompatActivity {

    static final int EDIT_ITEM = 0;
    boolean requestRefresh = false;

    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemNotesTitleTextView;
    TextView itemAddedDateTextView;
    TextView itemAddedDateTitleTextView;
    private Long itemID;
    String itemName;
    FoodItem food;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        dbHelper = new DatabaseHelper(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Item Details");

        itemNameTextView = findViewById(R.id.item_name);
        itemExpirationTextView = findViewById(R.id.item_expiration);
        itemNotesTitleTextView = findViewById(R.id.itemDetailsNoteTitle);
        itemNotesTextView = findViewById(R.id.itemDetailsNoteText);
        itemAddedDateTitleTextView = findViewById(R.id.itemDetailsPurchasedOnTitle);
        itemAddedDateTextView = findViewById(R.id.itemDetailsPurchasedOnText);

        //Extract resourceIDS
        itemName = getIntent().getStringExtra("ItemName");
        String itemExpiration = getIntent().getStringExtra("ItemExpiration");
        String itemNotes = getIntent().getStringExtra("ItemNotes");
        String itemAddedDate = getIntent().getStringExtra("ItemAddedDate");
        itemID = getIntent().getLongExtra("ItemId", -1);

        food = new FoodItem(
                itemName,
                itemNotes,
                itemAddedDate,
                itemExpiration
        );
        food.setId(itemID);
        populateComponents(food);
    }

    // Used to display custom Action Bar (with buttons)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Used to handle custom Action Bar button clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (requestRefresh)
                    setResult(RESULT_OK);
                else
                    setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.show_item_edit_button:
                onEditButtonClicked();
                break;
            case R.id.show_item_delete_button:
                onDeleteButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // When Delete Button is clicked
    public void onDeleteButtonClicked(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(itemName);
        alert.setMessage("Are you sure you want to delete " + itemName +"?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            //DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteItem(food.getId());
                finish();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    // When Edit Button is clicked
    public void onEditButtonClicked(){
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        intent.putExtra("userIntent", "editItem");
        intent.putExtra("food", itemID);
        startActivityForResult(intent, EDIT_ITEM);
    }

    private void populateComponents(FoodItem foodItem) {
        itemNameTextView.setText(foodItem.getName());
        itemExpirationTextView.setText(foodItem.getExpiryDate());

        if (foodItem.getNote().length() == 0) {
            itemNotesTitleTextView.setTextSize(0);
            itemNotesTextView.setTextSize(0);
        } else {
            itemNotesTextView.setText(foodItem.getNote());
            itemNotesTitleTextView.setTextSize(25);
            itemNotesTextView.setTextSize(30);
        }

        if (foodItem.getDateAdded().length() == 0) {
            itemAddedDateTitleTextView.setTextSize(0);
            itemAddedDateTextView.setTextSize(0);
        } else {
            itemAddedDateTextView.setText(foodItem.getDateAdded());
            itemAddedDateTitleTextView.setTextSize(25);
            itemAddedDateTextView.setTextSize(30);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "itemID=" + itemID + " requestCode="+ requestCode + " resultCode=" + resultCode);
        if (requestCode == EDIT_ITEM && resultCode == RESULT_OK) {
            requestRefresh = true;
            FoodItem dbFoodItem = dbHelper.getItemById(itemID);
            Log.d("onActivityResult", "itemID=" + itemID + " dbName="+ dbFoodItem.getName());
            populateComponents(dbFoodItem);
        }
    }
}
