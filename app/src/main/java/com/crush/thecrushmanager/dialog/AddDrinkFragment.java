package com.crush.thecrushmanager.dialog;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.MainDrink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends AddDialogFragment {

    public static final String TAG = "AddDrinkFragment";

    public interface OnSavingListener {
        void OnAddDrink(MainDrink drink);

        void OnUpdateDrink(DocumentReference reference, MainDrink drink);
    }

    private OnSavingListener mListener;

    @BindView(R.id.drink_form_name)
    TextView drinkName;

    @BindView(R.id.drink_form_price)
    TextView drinkPrice;

    @BindView(R.id.drink_form_image)
    ImageView drinkImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_drink, container, false);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        if (args != null) {
            mAction = (FORM_ACTION) args.getSerializable(FRAGMENT_ACTION);
            mObject = (FORM_OBJECT) args.getSerializable(FRAGMENT_OBJECT);
            getDialog().setTitle(args.getString(FRAGMENT_TITLE));
        }

        if (mSnapshot != null) {
            MainDrink drink = mSnapshot.toObject(MainDrink.class);
            mImageURL = Uri.parse(drink.getImageURL());
            Glide.with(drinkImage.getContext()).load(mImageURL).into(drinkImage);
            drinkName.setText(drink.getName());
            drinkPrice.setText(drink.getPrice() + "");
        }

        return rootView;
    }

    public static AddDrinkFragment newInstance() {
        AddDrinkFragment fragment = new AddDrinkFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Add Drink");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.ADD);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.DRINK);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddDrinkFragment newInstance(DocumentSnapshot snapshot) {
        AddDrinkFragment fragment = new AddDrinkFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Modify Drink");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.UPDATE);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.DRINK);
        fragment.setSnapshot(snapshot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mListener = (OnSavingListener) context;
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
        Log.d(TAG, "onDrinkFormSubmit: " + mListener);
        switch (mAction) {
            case ADD:
                MainDrink drink = new MainDrink(drinkName.getText().toString(), Long.valueOf(drinkPrice.getText().toString()), mImageURL.toString());

                if (mListener != null)
                    mListener.OnAddDrink(drink);
                break;
            case UPDATE:

                break;
        }
        dismiss();
    }

    @Override
    protected void loadImage() {
        Glide.with(drinkImage.getContext()).load(mFileUri).into(drinkImage);
    }
}
