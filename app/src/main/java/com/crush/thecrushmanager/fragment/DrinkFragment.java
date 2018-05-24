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

/**
 * A simple {@link Fragment} subclass.
 */
public class DrinkFragment extends Fragment implements CategoryAdapter.OnCategorySelectedListener, AddCategoryFragment.OnSavingListener {

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

        setHasOptionsMenu(true);

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


    @Override
    public void OnDeleting(DocumentSnapshot snapshot) {
        AlertDialog diaBox = ConfirmDeleteAction(snapshot);
        diaBox.show();
    }

    private AlertDialog ConfirmDeleteAction(final DocumentSnapshot snapshot) {
        AlertDialog myDeleteDialogBox = new AlertDialog.Builder(this.getContext())
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Bạn có chắc chắn xóa " + snapshot.get(Category.KEY_CATEGORY_NAME) + " không?")
                .setIcon(R.drawable.ic_recyclebin)

                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteCategory(snapshot.getReference());
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create();
        return myDeleteDialogBox;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.category_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.action_add_category:
                addFragment.setTargetFragment(this, REQUEST_CODE);
                addFragment.show(getFragmentManager(), AddCategoryFragment.TAG);
                return true;
            default:
                return false;
        }
    }


    private Task<Void> deleteCategory(final DocumentReference categoryRef) {

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.delete(categoryRef);
                return null;
            }
        });
    }


    private Task<Void> addCategory(final Category category) {
        final DocumentReference categoryRef = mFirestore.collection("categories").document();
        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.set(categoryRef, category);
                return null;
            }
        });
    }

    @Override
    public void OnAddCategory(Category category) {

        addCategory(category).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Category added");

                // Hide keyboard and scroll to top
                KeyboardUtils.hideKeyboard(getActivity());
                recyclerViewCategory.smoothScrollToPosition(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add category failed", e);

                // Show failure message and hide keyboard
                KeyboardUtils.hideKeyboard(getActivity());
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã có lỗi xảy ra!",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
