package com.crush.thecrushmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.crush.thecrushmanager.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

public class TempActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG ="TempActivity" ;
    private FirebaseFirestore mFirestore;
    private DocumentReference orderRef;
    private DocumentReference statusRef;

    ListenerRegistration orderRegistration;
    ListenerRegistration statusRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        mFirestore = FirebaseFirestore.getInstance();
        orderRef = mFirestore.collection("orders").document("QDzGGV8LUbN2gw9ZIgA2");

    }

    @Override
    protected void onStart() {
        super.onStart();
        orderRegistration = orderRef.addSnapshotListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


        statusRef = mFirestore.document( documentSnapshot.getDocumentReference("status").getPath());



        Log.d(TAG, "onEvent: document - " + documentSnapshot);
        Log.d(TAG, "onEvent: " + statusRef);

        statusRegistration = statusRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot2, @Nullable FirebaseFirestoreException e) {
                Toast.makeText(TempActivity.this,documentSnapshot2.getString("name"),Toast.LENGTH_LONG).show();
            }
        });
    }
}
