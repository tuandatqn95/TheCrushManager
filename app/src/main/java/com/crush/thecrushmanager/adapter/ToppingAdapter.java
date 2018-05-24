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
import com.crush.thecrushmanager.model.Topping;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tuand on 5/22/2018.
 */

public class ToppingAdapter extends FirestoreAdapter<ToppingAdapter.ViewHolder> {

    public interface OnToppingSelectedListener {
        void OnSelected(DocumentSnapshot snapshot);
        void OnDeleting(DocumentSnapshot snapshot);
    }

    private OnToppingSelectedListener mListener;

    public ToppingAdapter(Query query, OnToppingSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ToppingAdapter.ViewHolder(inflater.inflate(R.layout.item_topping_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.topping_item_image)
        ImageView toppingImage;

        @BindView(R.id.topping_item_name)
        TextView toppingName;

        @BindView(R.id.topping_item_price)
        TextView toppingPrice;

        @BindView(R.id.topping_item_delete)
        ImageView btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot) {
            Topping topping = snapshot.toObject(Topping.class);
            Glide.with(toppingImage.getContext()).load(topping.getImageURL()).into(toppingImage);
            toppingName.setText(topping.getName());
            toppingPrice.setText(topping.getPrice() + "");

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.OnDeleting(snapshot);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.OnSelected(snapshot);
                }
            });
        }
    }
}
