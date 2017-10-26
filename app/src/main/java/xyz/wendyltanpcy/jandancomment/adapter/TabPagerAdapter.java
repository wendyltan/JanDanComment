package xyz.wendyltanpcy.jandancomment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xyz.wendyltanpcy.jandancomment.TabFragment.CommentFragment;
import xyz.wendyltanpcy.jandancomment.TabFragment.GirlsFragment;
import xyz.wendyltanpcy.jandancomment.TabFragment.NewsFragment;
import xyz.wendyltanpcy.jandancomment.TabFragment.BoredFragment;

/**
 * Created by pannam on 2/26/2016.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                CommentFragment tab1 = new CommentFragment();
                return tab1;

            case 1:
                BoredFragment tab4 = new BoredFragment();
                return tab4;

            case 2:
                NewsFragment tab3 = new NewsFragment();
                return tab3;

            case 3:
                GirlsFragment tab2 = new GirlsFragment();
                return tab2;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
