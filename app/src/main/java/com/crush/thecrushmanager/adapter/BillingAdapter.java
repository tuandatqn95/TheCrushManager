package com.crush.thecrushmanager.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.OrderItem;
import com.crush.thecrushmanager.util.StringFormatUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillingAdapter extends FirestoreAdapter<BillingAdapter.ViewHolder> {

    private static final String TAG = "BillingAdapter";

    public BillingAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BillingAdapter.ViewHolder(inflater.inflate(R.layout.item_billing_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        holder.bind(getSnapshot(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.billing_item_drink_name)
        TextView drinkName;

        @BindView(R.id.billing_item_quantity)
        TextView quantity;

        @BindView(R.id.billing_item_price)
        TextView price;

        @BindView(R.id.billing_item_topping_recyclerview)
        RecyclerView recyclerViewTopping;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            background.setCornerRadius(25);
            background.setStroke(1, Color.BLACK);

            quantity.setBackground(background);
        }

        public void bind(DocumentSnapshot snapshot) {

            OrderItem orderItem = snapshot.toObject(OrderItem.class);
            Log.d(TAG, "bind: " + orderItem);
            drinkName.setText(orderItem.getMaindrink().getName());
            quantity.setText(orderItem.getQuantity() + "");
            price.setText(StringFormatUtils.FormatCurrency(orderItem.getPrice() * orderItem.getQuantity()));

            BillingToppingAdapter adapter = new BillingToppingAdapter(orderItem.getToppings(), orderItem.getQuantity());
            recyclerViewTopping.setAdapter(adapter);
            recyclerViewTopping.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewTopping.setItemAnimator(new DefaultItemAnimator());
        }
    }
}
