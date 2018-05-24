package com.crush.thecrushmanager.viewmodel;

import android.arch.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private boolean mIsSigningIn;

    public MainActivityViewModel( ) {
        this.mIsSigningIn = false;
    }

    public boolean isIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }
}
