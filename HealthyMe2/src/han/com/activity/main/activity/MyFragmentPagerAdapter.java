package han.com.activity.main.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import han.com.activity.main.fragment.FragmentReward;
import han.com.fragment.camera.FragmentCamera;
import han.com.fragment.goal.FragmentGoalList;
import han.com.fragment.share.FragmentShare;

/**
 *
 * @author han
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String className = MyFragmentPagerAdapter.class.getName();
    private final FragmentCamera fragmentCamera;
    private final FragmentShare fragmentShare;
    private final FragmentReward fragmentReward;
    private final FragmentGoalList fragmentGoal;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentReward = new FragmentReward();
        fragmentGoal = new FragmentGoalList();
        fragmentCamera = new FragmentCamera();
        fragmentShare = new FragmentShare();
    }

    @Override
    public Fragment getItem(int i) {
        Log.d(className, "Fragment " + i);

        if (i == 0) {
            return fragmentGoal;
        } else if (i == 1) {
            return fragmentReward;
        } else if (i == 2) {
            return fragmentCamera;
        } else if (i == 3) {
            return fragmentShare;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
