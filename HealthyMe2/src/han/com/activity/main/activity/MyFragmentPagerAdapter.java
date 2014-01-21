package han.com.activity.main.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import han.com.activity.main.fragment.FragmentHome;
import han.com.activity.main.fragment.FragmentReward;
import han.com.activity.main.fragment.FragmentTrack;

/**
 *
 * @author han
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String className = MyFragmentPagerAdapter.class.getName();
    private FragmentHome fragmentHome;
    private FragmentTrack fragmentTrack;
    private FragmentReward fragmentReward;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentTrack = new FragmentTrack();
        fragmentReward = new FragmentReward();
    }

    @Override
    public Fragment getItem(int i) {
        Log.d(className, "Fragment " + i);

        if (i == 0) {
            return fragmentTrack;
        } else if (i == 1) {
            return fragmentReward;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
