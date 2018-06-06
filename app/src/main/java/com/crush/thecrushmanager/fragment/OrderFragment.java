package com.crush.thecrushmanager.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.StatusFragmentAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment  {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.view_paper_order)
    ViewPager viewPager;



    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private StatusFragmentAdapter mAdapter;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("statuses").orderBy("order");
        mAdapter = new StatusFragmentAdapter(getChildFragmentManager(), mQuery);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);


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


}
