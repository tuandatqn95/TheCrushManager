package com.crush.thecrushmanager.dialog;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.model.Category;
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
public class AddCategoryFragment extends AddDialogFragment {

    public static final String TAG = "AddCategoryFragment";
    private FirebaseFirestore mFirestore;

    @BindView(R.id.category_form_name)
    TextView categoryName;

    @BindView(R.id.category_form_image)
    ImageView categoryImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

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
        Category category = new Category(categoryName.getText().toString(), mImageURL + "");
        switch (mAction) {
            case ADD:
                OnSaveCategory(category, null);
                break;
            case UPDATE:
                OnSaveCategory(category, mSnapshot.getReference());
                break;
        }
        dismiss();
    }


    @Override
    protected void loadImage() {
        Glide.with(categoryImage.getContext()).load(mFileUri).into(categoryImage);
    }


    public void OnSaveCategory(Category category, DocumentReference reference) {

        saveCategory(category, reference).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Category added");

                // Hide keyboard and scroll to top
//                KeyboardUtils.hideKeyboard(getActivity());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add category failed", e);

                // Show failure message and hide keyboard
//                KeyboardUtils.hideKeyboard(getActivity());
//                Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã có lỗi xảy ra!",
//                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private Task<Void> saveCategory(final Category category, final DocumentReference ref) {
        final DocumentReference categoryRef;
        if (ref == null)
            categoryRef = mFirestore.collection("categories").document();
        else
            categoryRef = ref;

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
}
