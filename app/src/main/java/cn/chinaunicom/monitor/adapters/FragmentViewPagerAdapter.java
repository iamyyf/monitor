package cn.chinaunicom.monitor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yfYang on 2017/10/30.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public FragmentViewPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
