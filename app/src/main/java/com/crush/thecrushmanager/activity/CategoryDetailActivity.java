package com.crush.thecrushmanager.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.adapter.DrinkAdapter;
import com.crush.thecrushmanager.dialog.AddDrinkFragment;
import com.crush.thecrushmanager.model.Category;
import com.crush.thecrushmanager.model.MainDrink;
import com.crush.thecrushmanager.util.KeyboardUtils;
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
        AddDrinkFragment.OnSavingListener {


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

    private AddDrinkFragment addDrinkFragment;


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

        addDrinkFragment = AddDrinkFragment.newInstance();

    }

    private void onCategoryLoaded(Category category) {
        categoryName.setText(category.getName());


        // Background image
        Glide.with(categoryTopCard.getContext())
                .load(category.getImageURL())
                .into(categoryTopCard);


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


        onCategoryLoaded(snapshot.toObject(Category.class));
    }

    @OnClick(R.id.category_button_back)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_add_drink_dialog)
    public void onAddDrinkClicked(View view) {
        addDrinkFragment.show(getSupportFragmentManager(), AddDrinkFragment.TAG);
    }

    @Override
    public void OnAddDrink(MainDrink drink) {

        addDrink(mCategoryRef, drink).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Drink added");

                // Hide keyboard and scroll to top
                KeyboardUtils.hideKeyboard(CategoryDetailActivity.this);
                recyclerDrinks.smoothScrollToPosition(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add drink failed", e);

                // Show failure message and hide keyboard
                KeyboardUtils.hideKeyboard(CategoryDetailActivity.this);
                Snackbar.make(findViewById(android.R.id.content), "Đã có lỗi xảy ra!",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnUpdateDrink(DocumentReference reference, MainDrink drink) {

    }

    private Task<Void> addDrink(DocumentReference mCategoryRef, final MainDrink drink) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference drinkRef = mCategoryRef.collection("maindrinks").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.set(drinkRef, drink);

                return null;
            }
        });
    }
}
