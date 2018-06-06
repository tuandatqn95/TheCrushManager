package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Customer;
import com.crush.thecrushmanager.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabInfoFragment extends Fragment {

    private static final String TAG = "OrderTabInfoFragment";

    @BindView(R.id.tab_info_name)
    TextView customerName;
    @BindView(R.id.tab_info_gender)
    TextView customerGender;
    @BindView(R.id.tab_info_phone)
    TextView customerPhone;
    @BindView(R.id.tab_info_email)
    TextView customerEmail;
    @BindView(R.id.tab_info_address)
    TextView customerAddress;

    @BindView(R.id.tab_info_ship)
    TextView shipAddress;
    @BindView(R.id.tab_info_note)
    TextView note;
    @BindView(R.id.cardNote)
    CardView cardNote;

    private DocumentSnapshot mSnapshot;
    private FirebaseFirestore mFirestore;

    public OrderTabInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewItem = inflater.inflate(R.layout.fragment_order_tab_info, container, false);
        ButterKnife.bind(this, viewItem);

        mFirestore = FirebaseFirestore.getInstance();

        Order order = mSnapshot.toObject(Order.class);
        shipAddress.setText(order.getAddress());
        if (TextUtils.isEmpty(order.getNote())) {
            cardNote.setVisibility(View.GONE);
        } else {
            cardNote.setVisibility(View.VISIBLE);
            note.setText(order.getNote());
        }

        mFirestore.collection("customers").document(order.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Customer customer = snapshot.toObject(Customer.class);
                customerName.setText(customer.getName());
                customerGender.setText(customer.getGender());
                customerPhone.setText(customer.getPhone());
                customerEmail.setText(customer.getEmail());
                customerAddress.setText(customer.getAddress());
            }
        });


        return viewItem;
    }

    public static OrderTabInfoFragment newFragment(DocumentSnapshot snapshot) {
        OrderTabInfoFragment fragment = new OrderTabInfoFragment();
        fragment.setSnapshot(snapshot);
        return fragment;
    }

    public void setSnapshot(DocumentSnapshot snapshot) {
        this.mSnapshot = snapshot;
    }

}
