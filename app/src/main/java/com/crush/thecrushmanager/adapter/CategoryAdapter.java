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
import com.crush.thecrushmanager.model.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tuand on 5/21/2018.
 */

public class CategoryAdapter extends FirestoreAdapter<CategoryAdapter.ViewHolder> {

    public interface OnCategorySelectedListener {
        void OnCategorySelected(DocumentSnapshot snapshot);

    }

    private OnCategorySelectedListener mListener;

    public CategoryAdapter(Query query, OnCategorySelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_category_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_item_image)
        ImageView categoryImage;

        @BindView(R.id.category_item_name)
        TextView categoryName;

        @BindView(R.id.quantity_text)
        TextView drinkQuantity;

     
        DrinkAdapter drinkAdapter;


        public ViewHolder(View itemView) {
            super(itemView);


            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnCategorySelectedListener mListener) {
            Category category = snapshot.toObject(Category.class);
            if (drinkAdapter == null)
                drinkAdapter = new DrinkAdapter(snapshot.getReference().collection("maindrinks")) {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        super.onEvent(documentSnapshots, e);
                        drinkQuantity.setText("Quantity: " + documentSnapshots.size());
                    }
                };
            drinkAdapter.setQuery(snapshot.getReference().collection("maindrinks"));
            drinkAdapter.startListening();


            Glide.with(categoryImage.getContext()).load(category.getImageURL()).into(categoryImage);
            categoryName.setText(category.getName());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mListener != null)
                        mListener.OnCategorySelected(snapshot);
                }
            });
        }
    }
}
