package xyz.wendyltanpcy.jandancomment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xyz.wendyltanpcy.jandancomment.Tab1Fragment;
import xyz.wendyltanpcy.jandancomment.Tab2Fragment;
import xyz.wendyltanpcy.jandancomment.Tab3Fragment;
import xyz.wendyltanpcy.jandancomment.Tab4Fragment;

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
                Tab1Fragment tab1 = new Tab1Fragment();
                return tab1;

            case 1:
                Tab4Fragment tab4 = new Tab4Fragment();
                return tab4;

            case 2:
                Tab3Fragment tab3 = new Tab3Fragment();
                return tab3;

            case 3:
                Tab2Fragment tab2 = new Tab2Fragment();
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
