package com.audsnap;

/**
 * Created by SONI's on 5/5/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[],int mNumbOfTabs) {
        super(fm);

        this.Titles=mTitles;
        this.NumbOfTabs=mNumbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0)
        {
            FriendsActivityFragment tab1 = new FriendsActivityFragment();
            return tab1;
        }
        else
        {
            InboxActivityFragment tab2= new InboxActivityFragment();
            return tab2;
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
