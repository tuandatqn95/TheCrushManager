package com.crush.thecrushmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.model.Topping;

import java.util.List;

public class BillingToppingAdapter extends RecyclerView.Adapter<BillingToppingAdapter.ViewHolder> {

    private List<Topping> toppingList;

    public BillingToppingAdapter(List<Topping> toppingList) {
        this.toppingList = toppingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
