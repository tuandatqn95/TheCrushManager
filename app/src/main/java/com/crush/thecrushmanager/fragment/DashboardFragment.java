package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Order;
import com.crush.thecrushmanager.util.StringFormatUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements EventListener<QuerySnapshot> {

    private static final String TAG = "DashboardFragment";

    @BindView(R.id.number_of_order)
    TextView numberOfOrder;

    @BindView(R.id.order_amount)
    TextView orderAmount;

    @BindView(R.id.average_order_amount)
    TextView averageOrderAmount;

    @BindView(R.id.total_revenue)
    TextView totalRevenue;

    private Query mQuery;
    private FirebaseFirestore mFirestore;
    private ListenerRegistration mRegistration;

    public DashboardFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewItem = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, viewItem);

        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("orders").whereEqualTo("status","DONE");


        return viewItem;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRegistration = mQuery.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }
        long numOfOrder = 0;
        long revenue = 0;
        long amount = 0;

        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            Order order = snapshot.toObject(Order.class);
            numOfOrder++;
            revenue += order.getTotalPrice();
            amount += order.getAmount();
        }

        double average = 0;
        if (numOfOrder != 0)
            average = amount / numOfOrder;
        numberOfOrder.setText(numOfOrder + "");
        orderAmount.setText(amount + "");
        totalRevenue.setText(StringFormatUtils.FormatCurrency(revenue));
        averageOrderAmount.setText(average + "");
    }
}
