package com.crush.thecrushmanager.dialog;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.MainDrink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends AddDialogFragment {

    public static final String TAG = "AddDrinkFragment";


    @BindView(R.id.drink_form_name)
    TextView drinkName;

    @BindView(R.id.drink_form_price)
    TextView drinkPrice;

    @BindView(R.id.drink_form_image)
    ImageView drinkImage;
    private FirebaseFirestore mFirestore;
    private DocumentReference mCategoryRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_drink, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            mAction = (FORM_ACTION) args.getSerializable(FRAGMENT_ACTION);
            mObject = (FORM_OBJECT) args.getSerializable(FRAGMENT_OBJECT);
            getDialog().setTitle(args.getString(FRAGMENT_TITLE));
        }

        if (mSnapshot != null) {
            MainDrink drink = mSnapshot.toObject(MainDrink.class);
            mImageURL = Uri.parse(drink.getImageURL() + "");
            Glide.with(drinkImage.getContext()).load(mImageURL).placeholder(R.drawable.default_drink).error(R.drawable.default_drink).into(drinkImage);
            drinkName.setText(drink.getName());
            drinkPrice.setText(drink.getPrice() + "");
        }

        return rootView;
    }

    public static AddDrinkFragment newInstance(DocumentReference categoryRef) {
        AddDrinkFragment fragment = new AddDrinkFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Add Drink");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.ADD);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.DRINK);
        fragment.setCategoryRef(categoryRef);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddDrinkFragment newInstance(DocumentReference categoryRef, DocumentSnapshot snapshot) {
        AddDrinkFragment fragment = new AddDrinkFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Modify Drink");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.UPDATE);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.DRINK);
        fragment.setSnapshot(snapshot);
        fragment.setCategoryRef(categoryRef);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @OnClick(R.id.drink_form_choose_btn)
    void onToppingChooseImage(View view) {
        launchCamera();
    }

    @OnClick(R.id.drink_form_cancel)
    void onDrinkFormCancel(View view) {
        dismiss();
    }

    @OnClick(R.id.drink_form_button)
    void onDrinkFormSubmit(View view) {
        if (TextUtils.isEmpty(drinkName.getText().toString())) {
            Toast.makeText(getContext(), "Please fill Drink's name!", Toast.LENGTH_SHORT).show();
            return;
        }
        MainDrink drink = new MainDrink(drinkName.getText().toString(), Long.valueOf("0" + drinkPrice.getText()), mImageURL + "", 0);

        switch (mAction) {
            case ADD:
                OnSaveDrink(mCategoryRef, drink, null);
                break;
            case UPDATE:
                OnSaveDrink(mCategoryRef, drink, mSnapshot.getReference());
                break;
        }
        dismiss();
    }


    public void OnSaveDrink(DocumentReference categoryRef, MainDrink drink, DocumentReference drinkRef) {

        saveDrink(categoryRef, drink, drinkRef).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Drink added");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add drink failed", e);


            }
        });
    }


    public void OnUpdateDrink(DocumentReference reference, MainDrink drink) {

    }

    private Task<Void> saveDrink(DocumentReference mCategoryRef, final MainDrink drink, DocumentReference ref) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference drinkRef;
        if (ref == null)
            drinkRef = mCategoryRef.collection("maindrinks").document();
        else
            drinkRef = ref;

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

    @Override
    protected void loadImage() {
        Glide.with(drinkImage.getContext()).load(mFileUri).placeholder(R.drawable.default_drink).error(R.drawable.default_drink).into(drinkImage);
    }

    public void setCategoryRef(DocumentReference categoryRef) {
        this.mCategoryRef = categoryRef;
    }
}
