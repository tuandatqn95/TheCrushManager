package com.crush.thecrushmanager.dialog;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.crush.thecrushmanager.R;
import com.crush.thecrushmanager.service.MyUploadService;
import com.google.firebase.firestore.DocumentSnapshot;

import static android.app.Activity.RESULT_OK;

public abstract class AddDialogFragment extends DialogFragment {

    public static final String TAG = "AddDialogFragment";
    protected static final String FRAGMENT_TITLE = "Fragment_Title";
    protected static final String FRAGMENT_ACTION = "Fragment_Action";
    protected static final String FRAGMENT_OBJECT = "Fragment_Object";

    private static final int RC_TAKE_PICTURE = 101;


    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive:" + intent);
            hideProgressDialog();

            switch (intent.getAction()) {

                case MyUploadService.UPLOAD_COMPLETED:
                case MyUploadService.UPLOAD_ERROR:
                    onUploadResultIntent(intent);
                    break;
            }
        }
    };


    protected FORM_ACTION mAction;
    protected FORM_OBJECT mObject;
    protected DocumentSnapshot mSnapshot;
    protected Uri mImageURL = null;
    protected Uri mFileUri = null;

    public enum FORM_ACTION {
        ADD,
        UPDATE
    }

    public enum FORM_OBJECT {
        CATEGORY,
        DRINK,
        TOPPING
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(getActivity(), "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;


        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        Intent intent = new Intent(getActivity(), MyUploadService.class);
        String dirName;
        switch (mObject) {
            case CATEGORY:
                dirName = "category_images";
                break;
            case DRINK:
                dirName = "drink_images";
                break;
            case TOPPING:
                dirName = "topping_images";
                break;
            default:
                dirName = "images";
        }
        intent.putExtra(MyUploadService.EXTRA_DIR_NAME, dirName);
        intent.putExtra(MyUploadService.EXTRA_FILE_URI, fileUri);
        intent.setAction(MyUploadService.ACTION_UPLOAD);
        getActivity().startService(intent);

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    protected void setSnapshot(DocumentSnapshot snapshot) {
        this.mSnapshot = snapshot;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    protected void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    protected void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mImageURL = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

        loadImage();
    }

    protected abstract void loadImage();


}
