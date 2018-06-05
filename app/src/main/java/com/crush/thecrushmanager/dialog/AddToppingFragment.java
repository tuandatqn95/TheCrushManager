package com.crush.thecrushmanager.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.crush.thecrushmanager.model.Topping;
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

public class AddToppingFragment extends AddDialogFragment {

    private static final String TAG = "AddToppingFragment";


    @BindView(R.id.topping_form_name)
    TextView toppingName;

    @BindView(R.id.topping_form_price)
    TextView toppingPrice;

    @BindView(R.id.topping_form_image)
    ImageView toppingImage;
    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_topping, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            mAction = (FORM_ACTION) args.getSerializable(FRAGMENT_ACTION);
            mObject = (FORM_OBJECT) args.getSerializable(FRAGMENT_OBJECT);
            getDialog().setTitle(args.getString(FRAGMENT_TITLE));
        }

        if (mSnapshot != null) {
            Topping topping = mSnapshot.toObject(Topping.class);
            mImageURL = Uri.parse(topping.getImageURL());
            Glide.with(toppingImage.getContext()).load(mImageURL).into(toppingImage);
            toppingName.setText(topping.getName());
            toppingPrice.setText(topping.getPrice() + "");
        }

        return rootView;
    }

    public static AddToppingFragment newInstance() {
        AddToppingFragment fragment = new AddToppingFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Add Topping");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.ADD);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.TOPPING);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddToppingFragment newInstance(DocumentSnapshot snapshot) {
        AddToppingFragment fragment = new AddToppingFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Modify Topping");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.UPDATE);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.TOPPING);
        fragment.setSnapshot(snapshot);
        fragment.setArguments(args);
        return fragment;
    }


    @OnClick(R.id.topping_form_choose_btn)
    void onToppingChooseImage(View view) {
        launchCamera();
    }


    @OnClick(R.id.topping_form_cancel)
    void onToppingFormCancel(View view) {
        dismiss();
    }

    @OnClick(R.id.topping_form_button)
    void onToppingFormSubmit(View view) {
        if (TextUtils.isEmpty(toppingName.getText().toString())) {
            Toast.makeText(getActivity(), "Please fill Topping's name!", Toast.LENGTH_SHORT).show();
            return;
        }
        Topping topping = new Topping(toppingName.getText().toString(), Long.valueOf("0" + toppingPrice.getText()), mImageURL + "");
        switch (mAction) {
            case ADD:
                OnSaveTopping(topping, null);

                break;
            case UPDATE:
                OnSaveTopping(topping, mSnapshot.getReference());

                break;
        }
        dismiss();
    }

    private Task<Void> saveTopping(final Topping topping, final DocumentReference ref) {
        final DocumentReference toppingRef;
        if (ref == null)
            toppingRef = mFirestore.collection("toppings").document();
        else
            toppingRef = ref;

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


    public void OnSaveTopping(Topping topping, DocumentReference reference) {

        saveTopping(topping, reference).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Topping added");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add topping failed", e);


            }
        });
    }


    @Override
    protected void loadImage() {
        Glide.with(toppingImage.getContext()).load(mFileUri).into(toppingImage);
    }
}
