package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.OrderAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabFragment extends Fragment {


    private static final String KEY_STATUS_ID = "STATUS_ID";

    @BindView(R.id.recycler_orders)
    RecyclerView recyclerOrder;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    OrderAdapter mAdapter;


    public OrderTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_tab, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("orders");

        mAdapter = new OrderAdapter(mQuery, new OrderAdapter.OnOrderSelectedListener() {

        });
        recyclerOrder.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerOrder.addItemDecoration(new DividerItemDecoration(recyclerOrder.getContext(), DividerItemDecoration.VERTICAL));
        recyclerOrder.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.stopListening();
    }

    public static OrderTabFragment newInstance(String statusId) {
        OrderTabFragment fragment = new OrderTabFragment();
        Bundle args = new Bundle();
        args.putString(OrderTabFragment.KEY_STATUS_ID, statusId);
        fragment.setArguments(args);
        return fragment;
    }

}
