package com.example.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Adapter knows how to display every data entry of the list in each row of the RecyclerView.
 * It knows they type of the view, its view layout and which data goes where in the view.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    Context context;
    List<String> items;
    OnLongClickListener onLongClickListener;
    OnClickListener onClickListener;
    String textColor = "#00000000"; //black


    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    public ItemsAdapter(Context context, List<String> items, OnLongClickListener onLongClickListener, OnClickListener onClickListener ) {
        this.context = context;
        this.items = items;
        this.onLongClickListener = onLongClickListener;
        this.onClickListener = onClickListener;
        System.out.println("inside Adapter"+items.toString());

    }

    // Responsible for creating view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View todoView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new ViewHolder(todoView);
    }

    // Responsible for binding data to a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        System.out.println("On Bind View Holder items.get(position)" +items.get(position));
        holder.bind(items.get(position));



    }

    // Tells the recycler view how many items are in the list
    @Override
    public int getItemCount() {
        System.out.println ("getItemCount CALLED : "+items.size());
        return items.size();
    }

    /**
     * Class-container to provide easy access to views that represent each row of the list.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
            tvItem.setTextColor(Color.BLACK);



        }

        /**
         * "Binds" data to TextView, effectively updates views with given values
         * that represent data for each individual row.
         *
         * @param todoText Text to be displayed for this to-do item.
         */
        public void bind(String todoText) {
           // String parts[] = todoText.split("/");


            //String task = parts[0];
           // String pri = parts[1];
           // String date = parts[3];
            tvItem.setText(todoText);






            // assign listeners to provide callbacks for user clicks/taps/press events
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Inform MainActivity that this item was long pressed
                    System.out.println("getAdapterPosition : "+getAdapterPosition());
                    onLongClickListener.onItemLongClicked(getAdapterPosition());
                    System.out.println("getAdapterPosition : "+getAdapterPosition());
                    return true;
                }
            });
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(getAdapterPosition());
                }
            });
        }
    }
}