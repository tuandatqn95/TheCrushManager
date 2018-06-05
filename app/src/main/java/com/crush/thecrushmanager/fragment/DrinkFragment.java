package com.crush.thecrushmanager.fragment;


import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.activity.CategoryDetailActivity;
import com.crush.thecrushmanager.adapter.CategoryAdapter;
import com.crush.thecrushmanager.dialog.AddCategoryFragment;
import com.crush.thecrushmanager.model.Category;
import com.crush.thecrushmanager.util.KeyboardUtils;
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
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrinkFragment extends Fragment implements CategoryAdapter.OnCategorySelectedListener {

    private static final String TAG = "DrinkFragment";
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.recycler_categories)
    RecyclerView recyclerViewCategory;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    CategoryAdapter mAdapter;

    private AddCategoryFragment addFragment;

    public DrinkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_drink, container, false);
        ButterKnife.bind(this, rootView);



        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} restaurants
        mQuery = mFirestore.collection("categories");

        mAdapter = new CategoryAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                Log.d(TAG, "onDataChanged");
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                super.onError(e);
                Snackbar.make(rootView.findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewCategory.setAdapter(mAdapter);

        addFragment = AddCategoryFragment.newInstance();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void OnCategorySelected(DocumentSnapshot snapshot) {
        Intent intent = new Intent(this.getContext(), CategoryDetailActivity.class);
        Bundle bundle = ActivityOptions.makeCustomAnimation(this.getContext(), R.anim.slide_in_from_right, R.anim.slide_out_to_left).toBundle();
        intent.putExtra(CategoryDetailActivity.PARAM_CATEGORY_ID, snapshot.getId());
        startActivity(intent, bundle);


    }

    @OnClick(R.id.fab_add_category_dialog)
    public void onAddCategory(View view){
        addFragment.setTargetFragment(this, REQUEST_CODE);
        addFragment.show(getFragmentManager(), AddCategoryFragment.TAG);
    }

}
