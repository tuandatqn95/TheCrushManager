package com.crush.thecrushmanager.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.OrderDetailAdapter;
import com.crush.thecrushmanager.model.Order;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "OrderDetailActivity";
    public static final String KEY_ORDER_ID = "order_id";


    private Query mQuery;
    private DocumentReference mOrderRef;
    private ListenerRegistration mOrderRegistration;
    private FirebaseFirestore mFirestore;

    private String mOrderId;

    @BindView(R.id.order_tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.order_viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.order_detail_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        mOrderId = getIntent().getStringExtra(KEY_ORDER_ID);

        mFirestore = FirebaseFirestore.getInstance();
        mOrderRef = mFirestore.collection("orders").document(mOrderId);


        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrderRegistration = mOrderRef.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mOrderRegistration != null) {
            mOrderRegistration.remove();
            mOrderRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            return;
        }
        onOrderLoaded(snapshot);
    }

    private void onOrderLoaded(DocumentSnapshot snapshot) {
        Order order = snapshot.toObject(Order.class);
//        getSupportActionBar().setTitle(order.getName());
        OrderDetailAdapter adapter = new OrderDetailAdapter(getSupportFragmentManager(), snapshot);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);
    }

}
