package com.crush.thecrushmanager.dialog;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Category;
import com.crush.thecrushmanager.model.Topping;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCategoryFragment extends AddDialogFragment {

    public static final String TAG = "AddCategoryFragment";


    public interface OnSavingListener {
        void OnAddCategory(Category category);
    }

    private OnSavingListener mListener;

    @BindView(R.id.category_form_name)
    TextView categoryName;

    @BindView(R.id.category_form_image)
    ImageView categoryImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        ButterKnife.bind(this, rootView);

        mListener = (OnSavingListener) getTargetFragment();

        Bundle args = getArguments();
        if (args != null) {
            mAction = (AddDialogFragment.FORM_ACTION) args.getSerializable(FRAGMENT_ACTION);
            mObject = (AddDialogFragment.FORM_OBJECT) args.getSerializable(FRAGMENT_OBJECT);
            getDialog().setTitle(args.getString(FRAGMENT_TITLE));
        }

        if (mSnapshot != null) {
            Category category = mSnapshot.toObject(Category.class);
            mImageURL = Uri.parse(category.getImageURL());
            Glide.with(categoryImage.getContext()).load(mImageURL).into(categoryImage);
            categoryName.setText(category.getName());
        }

        return rootView;
    }

    public static AddCategoryFragment newInstance() {
        AddCategoryFragment fragment = new AddCategoryFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Add Category");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.ADD);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.CATEGORY);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddCategoryFragment newInstance(DocumentSnapshot snapshot) {
        AddCategoryFragment fragment = new AddCategoryFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, "Modify Category");
        args.putSerializable(FRAGMENT_ACTION, FORM_ACTION.UPDATE);
        args.putSerializable(FRAGMENT_OBJECT, FORM_OBJECT.CATEGORY);
        fragment.setSnapshot(snapshot);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.category_form_choose_btn)
    void onToppingChooseImage(View view) {
        launchCamera();
    }

    @OnClick(R.id.category_form_cancel)
    void onCategoryFormCancel(View view) {
        dismiss();
    }

    @OnClick(R.id.category_form_button)
    void onCategoryFormSubmit(View view) {

        switch (mAction) {
            case ADD:
                Category category = new Category(categoryName.getText().toString(), "");

                if (mListener != null)
                    mListener.OnAddCategory(category);
                break;
            case UPDATE:

                break;
        }
        dismiss();
    }

    @Override
    protected void loadImage() {
        Glide.with(categoryImage.getContext()).load(mFileUri).into(categoryImage);

    }


}
