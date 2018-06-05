package com.crush.thecrushmanager.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.crush.thecrushmanager.fragment.OrderTabBillingFragment;
import com.crush.thecrushmanager.fragment.OrderTabCommentFragment;
import com.crush.thecrushmanager.fragment.OrderTabInfoFragment;
import com.crush.thecrushmanager.fragment.OrderTabLoggingFragment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends FragmentPagerAdapter {

    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private DocumentSnapshot mSnapshot;

    public OrderDetailAdapter(FragmentManager fm, DocumentSnapshot snapshot) {
        super(fm);
        this.mSnapshot = snapshot;
        addFragment(OrderTabBillingFragment.newFragment(snapshot), "Billing");
        addFragment(OrderTabInfoFragment.newFragment(snapshot), "Info");
        addFragment(OrderTabCommentFragment.newFragment(snapshot), "Comment");
        addFragment(OrderTabLoggingFragment.newFragment(snapshot), "History");
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    private void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }
}
