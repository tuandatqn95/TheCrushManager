package com.crush.thecrushmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.MainDrink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by tuand on 5/22/2018.
 */

public class DrinkAdapter extends FirestoreAdapter<DrinkAdapter.ViewHolder> {


    public DrinkAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DrinkAdapter.ViewHolder(inflater.inflate(R.layout.item_drink_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.drink_item_image)
        ImageView drinkImage;

        @BindView(R.id.drink_item_name)
        TextView drinkName;

        @BindView(R.id.drink_item_rating)
        MaterialRatingBar ratingBar;

        @BindView(R.id.drink_item_price)
        TextView drinkPrice;



        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(DocumentSnapshot snapshot) {
            MainDrink drink = snapshot.toObject(MainDrink.class);
            Glide.with(drinkImage.getContext()).load(drink.getImageURL()).into(drinkImage);
            drinkName.setText(drink.getName());
            drinkPrice.setText(drink.getPrice() + "");
            ratingBar.setRating((float) (new Random().nextInt(50)) / 10);
        }
    }
}
