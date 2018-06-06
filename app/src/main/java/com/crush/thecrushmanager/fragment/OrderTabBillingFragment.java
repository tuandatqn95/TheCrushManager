package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.BillingAdapter;
import com.crush.thecrushmanager.model.Order;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabBillingFragment extends Fragment implements View.OnClickListener {

    private DocumentSnapshot mOrderSnapshot;
    private Query mQuery;
    private BillingAdapter mAdapter;

    @BindView(R.id.fragment_order_recyclerview)
    RecyclerView recyclerViewBilling;

    @BindView(R.id.floating_menu)
    FloatingActionMenu floatingMenu;

    @BindView(R.id.menu_item_cancel)
    FloatingActionButton fabCancel;

    @BindView(R.id.menu_item_done)
    FloatingActionButton fabDone;

    @BindView(R.id.menu_item_delivery)
    FloatingActionButton fabToDelivery;

    @BindView(R.id.menu_item_confirm)
    FloatingActionButton fabConfirm;
    private FirebaseFirestore mFirestore;

    public OrderTabBillingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_tab_billing, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

        mQuery = mOrderSnapshot.getReference().collection("orderitems");
        mAdapter = new BillingAdapter(mQuery);
        recyclerViewBilling.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewBilling.addItemDecoration(new DividerItemDecoration(recyclerViewBilling.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewBilling.setAdapter(mAdapter);

        floatingMenu.setClosedOnTouchOutside(true);
        fabCancel.setOnClickListener(this);
        fabDone.setOnClickListener(this);
        fabToDelivery.setOnClickListener(this);
        fabConfirm.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        floatingMenu.close(true);
        switch (v.getId()) {
            case R.id.menu_item_cancel:
                OnUpdateStatusOrder(mOrderSnapshot, "CANCELED");
                break;
            case R.id.menu_item_confirm:
                OnUpdateStatusOrder(mOrderSnapshot, "CONFIRMED");
                break;
            case R.id.menu_item_delivery:
                OnUpdateStatusOrder(mOrderSnapshot, "TODELIVERY");
                break;
            case R.id.menu_item_done:
                OnUpdateStatusOrder(mOrderSnapshot, "DONE");
                break;

        }
    }

    private void OnUpdateStatusOrder(DocumentSnapshot snapshot, final String status) {
        Order order = snapshot.toObject(Order.class);
        order.setStatus(status);
        updateOrder(snapshot.getReference(), order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Task<Void> updateOrder(final DocumentReference orderRef, final Order order) {

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.set(orderRef, order);
                return null;
            }
        });
    }

}
