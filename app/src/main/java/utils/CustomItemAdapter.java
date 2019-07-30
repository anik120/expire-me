package utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;

import java.util.ArrayList;


public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.MyViewHolder> {

    // Receive the context from ItemListActivity
    private Context context;

    // ArrayList with the item expiration data points to populate adapter
    private ArrayList<FoodItem> items;

    // Database helper
    private DatabaseHelper dbHelper;

    // Filter for item list ("ALL", "SOON", "EXPIRED")
    private String listType;

    private ItemClickListener myClickListener;

    public CustomItemAdapter(Context context, String listType) {
        this.context = context;
        this.listType = listType;

        dbHelper = new DatabaseHelper(context);
        this.items = dbHelper.getFilteredItems(listType);
    }

    // GETTER/SETTER METHODS

    // Getter for context
    public Context getContext() {
        return context;
    }

    // Get FoodItem at given position
    public FoodItem getItem(int position) {
        return items.get(position);
    }

    // DATABASE METHODS

    // Refreshes list of items
    public void refreshItems() {
        this.items = dbHelper.getFilteredItems(listType);
        notifyDataSetChanged();
    }

    // Deletes item from DB and list and refreshes view
    public void deleteItem(int position) {
        dbHelper.deleteItem(items.get(position).getId());
        items.remove(position);
        this.notifyDataSetChanged();
    }

    // RECYCLERVIEW ADAPTER OVERRIDE METHODS

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_adapter_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemExpiration.setText(item.getExpiryDate());
        holder.itemCountdown.setText(getCountdownText(item.daysUntilExpiration()));
        holder.itemCountdown.setTextColor(getCountdownColor(item.daysUntilExpiration()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // HELPER METHODS

    // Gets text to display for itemCountdown
    private String getCountdownText(long diffInDays) {
        if (diffInDays < 0) {
            return "!";
        } else if (diffInDays < 1) {
            return "Today!";
        }else if (diffInDays == 1) {
            return "1 day";
        } else if (diffInDays < 30){
            return diffInDays + " days";
        } else if ((diffInDays / 30) == 1) {
            return "1 month";
        } else {
            return (diffInDays / 30) + " months";
        }
    }

    // Gets color to display for countdown text
    private int getCountdownColor(long diffInDays) {
        if (diffInDays < 0) {
            return Color.RED;
        } else if (diffInDays <= 3){
            return Color.rgb(235, 140, 52);
        } else {
            return Color.rgb(52, 235, 61);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemName;
        TextView itemExpiration;
        TextView itemCountdown;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameAdapterItem);
            itemExpiration = itemView.findViewById(R.id.itemExpirationAdapterItem);
            itemCountdown = itemView.findViewById(R.id.itemCountdownAdapterItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myClickListener != null) myClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.myClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
