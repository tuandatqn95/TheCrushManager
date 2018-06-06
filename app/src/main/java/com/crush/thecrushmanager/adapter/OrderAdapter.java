package com.crush.thecrushmanager.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Customer;
import com.crush.thecrushmanager.model.Order;
import com.crush.thecrushmanager.model.Status;
import com.crush.thecrushmanager.util.StringFormatUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TuanDat on 5/23/2018.
 */

public class OrderAdapter extends FirestoreAdapter<OrderAdapter.ViewHolder> {

    private static final String TAG = "OrderAdapter";

    public interface OnOrderSelectedListener {
        void OnOrderSelected(DocumentSnapshot snapshot);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

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
        private FirebaseFirestore mFirestore;
        private PrettyTime prettyTime = new PrettyTime();

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            statusBackground = new GradientDrawable();
            statusBackground.setShape(GradientDrawable.RECTANGLE);
            statusBackground.setCornerRadius(15.0f);
//            statusBackground.setStroke(2, Color.BLACK);
            statusName.setBackground(statusBackground);
            mFirestore = FirebaseFirestore.getInstance();
        }

        public void bind(final DocumentSnapshot snapshot, final OnOrderSelectedListener mListener) {
            Order order = snapshot.toObject(Order.class);

            lastModified.setText(prettyTime.format(order.getCreateOn()));
            orderPrice.setText(StringFormatUtils.FormatCurrency(order.getTotalPrice()));
            statusName.setText(order.getStatus());

            //Load customer info
            mFirestore.collection("customers").document(order.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    Log.d(TAG, "onSuccess: customer - " + snapshot);
                    Customer customer = snapshot.toObject(Customer.class);
//                    Glide.with(customerImage.getContext()).load(customer.getImageURL()).into(customerImage);
                    customerName.setText(customer.getName());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    customerName.setText("#Error!");
                }
            });

            mFirestore.collection("statuses").document(order.getStatus()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    Log.d(TAG, "onSuccess: status - " + snapshot);
                    Status status = snapshot.toObject(Status.class);
                    statusBackground.setColor(Color.parseColor(status.getColor()));
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.OnOrderSelected(snapshot);
                }
            });
        }
    }
}
