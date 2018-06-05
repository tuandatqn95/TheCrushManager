package com.crush.thecrushmanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.activity.OrderDetailActivity;
import com.crush.thecrushmanager.adapter.OrderAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabFragment extends Fragment {


    private static final String PARAM_STATUS_ID = "PARAM_STATUS_ID";

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

        String statusId = null;
        if (getArguments() != null) {
            statusId = getArguments().getString(PARAM_STATUS_ID);
        }

        mFirestore = FirebaseFirestore.getInstance();
        if (statusId.equals("ALL"))
            mQuery = mFirestore.collection("orders");
        else
            mQuery = mFirestore.collection("orders").whereEqualTo("status", statusId);


        mAdapter = new OrderAdapter(mQuery, new OrderAdapter.OnOrderSelectedListener() {

            @Override
            public void OnOrderSelected(DocumentSnapshot snapshot) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, snapshot.getId());
                startActivity(intent);
            }
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
        args.putString(OrderTabFragment.PARAM_STATUS_ID, statusId);
        fragment.setArguments(args);
        return fragment;
    }

}
