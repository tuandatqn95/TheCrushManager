package com.crush.thecrushmanager.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Order;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TuanDat on 5/23/2018.
 */

public class OrderAdapter extends FirestoreAdapter<OrderAdapter.ViewHolder> {

    public interface OnOrderSelectedListener {

    }

    private OnOrderSelectedListener mListener;

    public OrderAdapter(Query query, OnOrderSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new OrderAdapter.ViewHolder(inflater.inflate(R.layout.item_order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_item_image)
        ImageView customerImage;

        @BindView(R.id.order_item_customer_name)
        TextView customerName;

        @BindView(R.id.order_item_time)
        TextView lastModified;

        @BindView(R.id.order_item_price)
        TextView orderPrice;

        @BindView(R.id.order_item_status)
        TextView statusName;

        GradientDrawable statusBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            statusBackground = new GradientDrawable();
            statusBackground.setShape(GradientDrawable.RECTANGLE);
            statusBackground.setCornerRadius(15.0f);
//            statusBackground.setStroke(2, Color.BLACK);
            statusName.setBackground(statusBackground);

        }

        public void bind(DocumentSnapshot snapshot, OnOrderSelectedListener mListener) {
            Order order = snapshot.toObject(Order.class);
            Glide.with(customerImage.getContext()).load(order.getImageURL()).into(customerImage);

            customerName.setText(order.getName());
            lastModified.setText(new Date().getTime() + "");
            orderPrice.setText("50.000");
            statusName.setText("Đã thanh toán");
            statusBackground.setColor(Color.RED);


        }
    }
}
