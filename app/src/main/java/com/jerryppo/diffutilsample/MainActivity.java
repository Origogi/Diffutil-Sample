package com.jerryppo.diffutilsample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

        Set<Integer> set = new HashSet<>();


        Button add = findViewById(R.id.add);
        add.setOnClickListener(v -> {


                    int randomValue;

                    do {
                        randomValue = random.nextInt(10000);
                    }
                    while (set.contains(randomValue));


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
                        int id = adapter.remove(index);
                        set.remove(id);
                    }

                }
        );

        Button sort = findViewById(R.id.sort);
        sort.setOnClickListener(v -> {
            adapter.sort();
        });

        Button like = findViewById(R.id.like);
        like.setOnClickListener(v -> {
            int randomValue = random.nextInt(10000);
            int size = adapter.items.size();

            if (size > 0) {
                int index = randomValue % size;
                adapter.updateLikeOrUnLike(index);
            }
        });


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

        Item oldItem = oldItems.get(oldIndex);
        Item newItem = newItems.get(newIndex);
        return oldItem.title.equals(newItem.title);
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

    Item(String title, boolean like) {
        this.title = title;
        this.like = like;
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


        List<Item> newItems = new ArrayList<>(items);
        newItems.add(item);

        update(newItems);
    }

    void update(List<Item> newItems) {
        List<Item> oldItems = items;

        DiffCallback diffCallback = new DiffCallback(newItems, oldItems);

        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        items.clear();
        items.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);

    }

    public int remove(int index) {
        List<Item> newItems = new ArrayList<>(items);

        int id = Integer.parseInt(newItems.remove(index).title);
        update(newItems);

        return id;

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void sort() {
        List<Item> newItems = new ArrayList<>(items);
        newItems.sort((a, b) -> {
            return Integer.parseInt(a.title) - Integer.parseInt(b.title);
        });

        update(newItems);

    }

    public void updateLikeOrUnLike(int index) {
        List<Item> newItems = new ArrayList<>(items);
        Item item = newItems.remove(index);

        newItems.add(index, new Item(item.title, !item.like));
        update(newItems);

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


