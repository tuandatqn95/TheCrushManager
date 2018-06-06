package com.crush.thecrushmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Topping;
import com.crush.thecrushmanager.util.RoundedCornersTransformation;
import com.crush.thecrushmanager.util.StringFormatUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tuand on 5/22/2018.
 */

public class ToppingAdapter extends FirestoreAdapter<ToppingAdapter.ViewHolder> {


    public ToppingAdapter(Query query) {
        super(query);

    }

    public RecyclerViewMenuContextInfo getMenuInfo() {
        return mMenuInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ToppingAdapter.ViewHolder(inflater.inflate(R.layout.item_topping_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mMenuInfo, position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {


        @BindView(R.id.topping_item_image)
        ImageView toppingImage;

        @BindView(R.id.topping_item_name)
        TextView toppingName;

        @BindView(R.id.topping_item_price)
        TextView toppingPrice;

        private RoundedCornersTransformation transformer;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            ButterKnife.bind(this, itemView);
            transformer = new RoundedCornersTransformation(toppingImage.getContext(), 5, 2);
        }

        public void bind(final DocumentSnapshot snapshot, final RecyclerViewMenuContextInfo mMenuInfo, final int position) {
            Topping topping = snapshot.toObject(Topping.class);
            Glide.with(toppingImage.getContext()).load(topping.getImageURL()).dontAnimate().placeholder(R.drawable.default_topping).error(R.drawable.default_topping).
                    bitmapTransform(transformer).into(toppingImage);
            toppingName.setText(topping.getName());
            toppingPrice.setText(StringFormatUtils.FormatCurrency(topping.getPrice()));

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mMenuInfo.position = position;
                    return false;
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }
}
