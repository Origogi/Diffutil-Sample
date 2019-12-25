package com.jerryppo.diffutilsample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Adapter adapter = new Adapter(this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Random random = new Random();


        Button add = findViewById(R.id.add);
        add.setOnClickListener(v -> {
                    int randomValue = random.nextInt(10000);
                    Item item = new Item(randomValue + "");
                    adapter.addItem(item);
                }
        );

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
                    int randomValue = random.nextInt(10000);
                    int size = adapter.items.size();

                    if (size > 0) {
                        int index = randomValue % size;
                        adapter.remove(index);
                    }

                }
        );


    }
}

class DiffCallback extends DiffUtil.Callback {

    List<Item> newItems;
    List<Item> oldItems;


    DiffCallback(List<Item> newItems, List<Item> oldItems) {
        this.newItems = newItems;
        this.oldItems = oldItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldIndex, int newIndex) {
        System.out.println("hello11");
        return newItems.get(newIndex).equals(oldItems.get(oldIndex));
    }

    @Override
    public boolean areContentsTheSame(int oldIndex, int newIndex) {
        System.out.println("hello22");

        Item oldItem = oldItems.get(oldIndex);
        Item newItem = newItems.get(newIndex);

        return (oldItem.title.equals(newItem.title)) && oldItem.like == newItem.like;
    }
}

class Item {
    String title = "";
    boolean like = false;

    Item(String title) {
        this.title = title;
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    ImageView delete;
    TextView textView;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imageView);
        textView = itemView.findViewById(R.id.textView);
        delete = itemView.findViewById(R.id.deleteButton);
    }
}

class Adapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Item> items = new ArrayList<>();

    Context context;

    Adapter(Context context) {
        this.context = context;
    }

    void addItem(Item item) {
        List<Item> oldItems = items;
        List<Item> newItems = new ArrayList<>(items);
        newItems.add(item);


        DiffCallback diffCallback = new DiffCallback(newItems, oldItems);

        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        items.clear();
        items.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);


    }

    public void remove(int index) {
        List<Item> oldItems = items;
        List<Item> newItems = new ArrayList<>(items);

        System.out.println("remove : " + index);
        newItems.remove(index);

        DiffCallback diffCallback = new DiffCallback(newItems, oldItems);

        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        items.clear();
        items.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup
                , false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Item item = items.get(i);

        if (item.like) {
            myViewHolder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_24dp));
        } else {
            myViewHolder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp));

        }

        myViewHolder.imageView.setOnClickListener(v -> {
            int index = items.indexOf(item);

            item.like = !item.like;
            notifyItemChanged(index);

        });

        myViewHolder.delete.setOnClickListener(v -> {
            int index = items.indexOf(item);

            System.out.println(index);
            remove(index);
        });

        myViewHolder.textView.setText(item.title);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}


