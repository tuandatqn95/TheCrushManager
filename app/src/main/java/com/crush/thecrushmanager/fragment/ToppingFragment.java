package com.crush.thecrushmanager.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
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
import com.crush.thecrushmanager.adapter.ToppingAdapter;
import com.crush.thecrushmanager.dialog.AddCategoryFragment;
import com.crush.thecrushmanager.dialog.AddToppingFragment;
import com.crush.thecrushmanager.model.Topping;
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
public class ToppingFragment extends Fragment implements ToppingAdapter.OnToppingSelectedListener, AddToppingFragment.OnSavingListener {

    private static final String TAG = "ToppingFragment";
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.recycler_toppings)
    RecyclerView recyclerToppings;

    private FirebaseFirestore mFirestore;


    Query mQuery;
    ToppingAdapter mAdapter;
    private AddToppingFragment addToppingFragment;

    public ToppingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_topping, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        mFirestore = FirebaseFirestore.getInstance();

        mQuery = mFirestore.collection("toppings");

        mAdapter = new ToppingAdapter(mQuery, this) {
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

        recyclerToppings.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerToppings.addItemDecoration(new DividerItemDecoration(recyclerToppings.getContext(), DividerItemDecoration.VERTICAL));

        recyclerToppings.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.topping_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.action_add_topping:
                addToppingFragment = AddToppingFragment.newInstance();
                addToppingFragment.setTargetFragment(this, REQUEST_CODE);
                addToppingFragment.show(getFragmentManager(), AddCategoryFragment.TAG);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void OnSelected(DocumentSnapshot snapshot) {

        addToppingFragment = AddToppingFragment.newInstance(snapshot);
        addToppingFragment.setTargetFragment(this, REQUEST_CODE);
        addToppingFragment.show(getFragmentManager(), AddCategoryFragment.TAG);
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
                .setMessage("Bạn có chắc chắn xóa " + snapshot.get(Topping.KEY_TOPPING_NAME) + " không?")
                .setIcon(R.drawable.ic_recyclebin)

                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteTopping(snapshot.getReference());
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


    private Task<Void> deleteTopping(final DocumentReference toppingRef) {

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.delete(toppingRef);
                return null;
            }
        });
    }

    private Task<Void> addTopping(final Topping topping) {
        final DocumentReference toppingRef = mFirestore.collection("toppings").document();
        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.set(toppingRef, topping);
                return null;
            }
        });
    }

    private Task<Void> updateTopping(final DocumentReference toppingRef, final Topping topping) {
        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.set(toppingRef, topping);
                return null;
            }
        });
    }

    @Override
    public void OnAddTopping(Topping topping) {

        addTopping(topping).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Topping added");

                // Hide keyboard and scroll to top
                KeyboardUtils.hideKeyboard(getActivity());
                recyclerToppings.smoothScrollToPosition(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add topping failed", e);

                // Show failure message and hide keyboard
                KeyboardUtils.hideKeyboard(getActivity());
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã có lỗi xảy ra!",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnUpdateTopping(DocumentReference reference, Topping topping) {
        updateTopping(reference, topping);
    }


}
