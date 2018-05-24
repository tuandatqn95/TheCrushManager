package com.crush.thecrushmanager.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Topping;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddToppingFragment extends AddDialogFragment {

    private static final String TAG = "AddToppingFragment";

    public interface OnSavingListener {
        void OnAddTopping(Topping topping);
        void OnUpdateTopping(DocumentReference reference, Topping topping);
    }

    private OnSavingListener mListener;

    @BindView(R.id.topping_form_name)
    TextView toppingName;

    @BindView(R.id.topping_form_price)
    TextView toppingPrice;

    @BindView(R.id.topping_form_image)
    ImageView toppingImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_topping, container, false);
        ButterKnife.bind(this, rootView);

        mListener = (OnSavingListener) getTargetFragment();

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

        Topping topping = new Topping(toppingName.getText().toString(), Long.valueOf(toppingPrice.getText().toString()), mImageURL.toString());
        switch (mAction) {
            case ADD:

                if (mListener != null)
                    mListener.OnAddTopping(topping);
                break;
            case UPDATE:
                if (mListener != null)
                    mListener.OnUpdateTopping(mSnapshot.getReference(), topping);
                break;
        }
        dismiss();
    }

    @Override
    protected void loadImage() {
        Glide.with(toppingImage.getContext()).load(mFileUri).into(toppingImage);
    }
}
