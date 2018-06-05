package com.crush.thecrushmanager.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.DrinkAdapter;
import com.crush.thecrushmanager.dialog.AddCategoryFragment;
import com.crush.thecrushmanager.dialog.AddDrinkFragment;
import com.crush.thecrushmanager.model.Category;
import com.crush.thecrushmanager.model.MainDrink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CategoryDetailActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>,
        PopupMenu.OnMenuItemClickListener {


    private static final String TAG = "CategoryDetailActivity";
    public static final String PARAM_CATEGORY_ID = "category_id";

    @BindView(R.id.category_image)
    ImageView categoryTopCard;

    @BindView(R.id.category_name)
    TextView categoryName;

    @BindView(R.id.recycler_drinks)
    RecyclerView recyclerDrinks;

    private FirebaseFirestore mFirestore;
    private DocumentReference mCategoryRef;
    private ListenerRegistration mCategoryRegistration;
    private DrinkAdapter mAdapter;
    private DocumentSnapshot mSnapshot;
    private AddDrinkFragment addDrinkFragment;
    private AddDrinkFragment editDrinkFragment;
    private AddCategoryFragment editCategoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        ButterKnife.bind(this);

        String categoryId = getIntent().getExtras().getString(PARAM_CATEGORY_ID);
        if (categoryId == null)
            throw new IllegalArgumentException("Must pass extra " + PARAM_CATEGORY_ID);


        mFirestore = FirebaseFirestore.getInstance();
        mCategoryRef = mFirestore.collection("categories").document(categoryId);
        Query drinksQuery = mCategoryRef.collection("maindrinks");

        mAdapter = new DrinkAdapter(drinksQuery) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();
            }
        };

        recyclerDrinks.setLayoutManager(new LinearLayoutManager(this));
        recyclerDrinks.addItemDecoration(new DividerItemDecoration(recyclerDrinks.getContext(), DividerItemDecoration.VERTICAL));
        recyclerDrinks.setAdapter(mAdapter);


        registerForContextMenu(recyclerDrinks);


    }

    private void onCategoryLoaded(Category category) {

        if (category == null)
            return;

        categoryName.setText(category.getName());

        // Background image
        Glide.with(categoryTopCard.getContext())
                .load(category.getImageURL())
                .into(categoryTopCard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter.startListening();
        mCategoryRegistration = mCategoryRef.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAdapter.stopListening();

        if (mCategoryRegistration != null) {
            mCategoryRegistration.remove();
            mCategoryRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        Log.d(TAG, "onEvent()");

        if (e != null) {
            Log.w(TAG, "category:onEvent", e);
            return;
        }

        this.mSnapshot = snapshot;

        editCategoryFragment = AddCategoryFragment.newInstance(mSnapshot);
        onCategoryLoaded(snapshot.toObject(Category.class));
    }

    @OnClick(R.id.category_button_back)
    public void onBackArrowClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_add_drink_dialog)
    public void onAddDrinkClicked(View view) {
        addDrinkFragment = AddDrinkFragment.newInstance(mCategoryRef);
        addDrinkFragment.show(getSupportFragmentManager(), AddDrinkFragment.TAG);
    }

    @OnClick(R.id.category_button_edit)
    public void onEditCategoryClick(View view) {

        PopupMenu popup = new PopupMenu(this, view);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.category_modify_menu);
        popup.show();


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_category:
                editCategoryFragment.show(getSupportFragmentManager(), AddCategoryFragment.TAG);
                return true;
            case R.id.action_delete_category:
                AlertDialog diaBox = ConfirmCategoryDeleteAction(mSnapshot);
                diaBox.show();
                return true;
            default:
                return false;
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.drink_modify_menu, menu);
        menu.setHeaderTitle("Drink");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        DrinkAdapter.RecyclerViewMenuContextInfo info = mAdapter.getMenuInfo();
        DocumentSnapshot drinkSnapshot = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_edit_drink:
                editDrinkFragment = AddDrinkFragment.newInstance(mCategoryRef, drinkSnapshot);
                editDrinkFragment.show(getSupportFragmentManager(), AddDrinkFragment.TAG);
                break;
            case R.id.action_delete_drink:
                AlertDialog diaBox = ConfirmDrinkDeleteAction(drinkSnapshot);
                diaBox.show();
                break;
        }
        return super.onContextItemSelected(item);
    }


    private AlertDialog ConfirmCategoryDeleteAction(final DocumentSnapshot snapshot) {
        Category category = snapshot.toObject(Category.class);
        AlertDialog myDeleteDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete category '" + category.getName() + "'?")
                .setIcon(R.drawable.ic_recyclebin)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteCategory(snapshot.getReference()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CategoryDetailActivity.this, "Delete category success!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CategoryDetailActivity.this, "Delete category failure!", Toast.LENGTH_SHORT).show();

                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myDeleteDialogBox;
    }

    private AlertDialog ConfirmDrinkDeleteAction(final DocumentSnapshot snapshot) {
        MainDrink drink = snapshot.toObject(MainDrink.class);
        AlertDialog myDeleteDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete drink '" + drink.getName() + "'?")
                .setIcon(R.drawable.ic_recyclebin)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteDrink(snapshot.getReference()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CategoryDetailActivity.this, "Delete drink success!", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CategoryDetailActivity.this, "Delete drink failure!", Toast.LENGTH_SHORT).show();

                            }
                        });
                        dialog.dismiss();

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myDeleteDialogBox;
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

    private Task<Void> deleteDrink(final DocumentReference drinkRef) {

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.delete(drinkRef);
                return null;
            }
        });
    }

}
