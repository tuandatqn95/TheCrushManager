package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.BillingAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabBillingFragment extends Fragment {

    private DocumentSnapshot mOrderSnapshot;
    private Query mQuery;
    private BillingAdapter mAdapter;

    @BindView(R.id.fragment_order_recyclerview)
    RecyclerView recyclerViewBilling;

    public OrderTabBillingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_tab_billing, container, false);
        ButterKnife.bind(this, rootView);
        mQuery = mOrderSnapshot.getReference().collection("orderitem");
        mAdapter = new BillingAdapter(mQuery);
        recyclerViewBilling.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewBilling.addItemDecoration(new DividerItemDecoration(recyclerViewBilling.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewBilling.setAdapter(mAdapter);
        return rootView;
    }

    public static OrderTabBillingFragment newFragment(DocumentSnapshot snapshot) {
        OrderTabBillingFragment fragment = new OrderTabBillingFragment();
        fragment.setSnapshot(snapshot);
        return fragment;
    }

    public void setSnapshot(DocumentSnapshot snapshot) {
        this.mOrderSnapshot = snapshot;
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
}
