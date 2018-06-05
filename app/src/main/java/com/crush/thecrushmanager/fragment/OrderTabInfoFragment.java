package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTabInfoFragment extends Fragment {


    private DocumentSnapshot mSnapshot;

    public OrderTabInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_tab_info, container, false);
    }

    public static OrderTabInfoFragment newFragment(DocumentSnapshot snapshot){
        OrderTabInfoFragment fragment = new OrderTabInfoFragment();
        fragment.setSnapshot(snapshot);
        return  fragment;
    }

    public void setSnapshot(DocumentSnapshot snapshot) {
        this.mSnapshot = snapshot;
    }

}
