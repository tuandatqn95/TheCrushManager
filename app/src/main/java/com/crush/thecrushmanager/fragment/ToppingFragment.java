package com.crush.thecrushmanager.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.DrinkAdapter;
import com.crush.thecrushmanager.adapter.ToppingAdapter;
import com.crush.thecrushmanager.dialog.AddCategoryFragment;
import com.crush.thecrushmanager.dialog.AddDrinkFragment;
import com.crush.thecrushmanager.dialog.AddToppingFragment;
import com.crush.thecrushmanager.model.Topping;
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
public class ToppingFragment extends Fragment  {

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

        mFirestore = FirebaseFirestore.getInstance();

        mQuery = mFirestore.collection("toppings");

        mAdapter = new ToppingAdapter(mQuery) {
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

        registerForContextMenu(recyclerToppings);

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

    @OnClick(R.id.fab_add_topping_dialog)
    public void onAddCategory(View view) {
        addToppingFragment = AddToppingFragment.newInstance();
        addToppingFragment.show(getFragmentManager(), AddCategoryFragment.TAG);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.topping_modify_menu, menu);
        menu.setHeaderTitle("Topping");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ToppingAdapter.RecyclerViewMenuContextInfo info = mAdapter.getMenuInfo();
        DocumentSnapshot drinkSnapshot = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_edit_topping:
                addToppingFragment = AddToppingFragment.newInstance(drinkSnapshot);
                addToppingFragment.show(getFragmentManager(), AddDrinkFragment.TAG);
                break;
            case R.id.action_delete_topping:
                AlertDialog diaBox = ConfirmDeleteAction(drinkSnapshot);;
                diaBox.show();
                break;
        }
        return super.onContextItemSelected(item);
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


}
