package han.com.activity.main.activity;

import android.app.*;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import han.com.GCMIntentService;
import han.com.R;
import han.com.activity.main.fragment.FragmentHome;
import han.com.activity.reward.welcome.ActivityRewardWelcome;
import han.com.activity.track.reminder.ProgressReminder;
import han.com.datapool.MyPedometerPreferences;
import han.com.datapool.MySharedPreferences;
import han.com.db.DatabaseHandler;
import han.com.music.MusicPlayer;
import han.com.pedometer.Pedometer;
import han.com.pedometer.StepService;
import han.com.resources.ResourceGetter;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;
import han.com.utils.Values;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String className = MainActivity.class.getName();
    private static boolean hasDestroy;

    public static boolean isHasDestroy() {
        return hasDestroy;
    }
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager viewPager;
    private int previousRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");
        setContentView(R.layout.activity_main_container);
        hasDestroy = false;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.d(className, "density: " + metrics.density);
        Log.d(className, "heightPixels: " + metrics.heightPixels);
        Log.d(className, "widthPixels: " + metrics.widthPixels);
        Values.DisplayDensity = metrics.density;

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        ActionBar.Tab tabTrack = actionBar.newTab();
        tabTrack.setIcon(R.drawable.ic_action_track);
        tabTrack.setTabListener(this);
        actionBar.addTab(tabTrack);

        ActionBar.Tab tabReward = actionBar.newTab();
        tabReward.setIcon(R.drawable.ic_action_reward3);
        tabReward.setTabListener(this);
        actionBar.addTab(tabReward);

//        ActionBar.Tab tabFriends = actionBar.newTab();
//        tabFriends.setIcon(R.drawable.ic_action_friends);
//        tabFriends.setTabListener(this);
//        actionBar.addTab(tabFriends);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

//        if (rotation != previousRotation) {
//            previousRotation = rotation;
//            getActionBar().hide();
//        }

        //init preferences and services
        initServices();

        ProgressReminder r = (ProgressReminder) MySharedPreferences.getInstance(this)
                .getPreferenceItem(MySharedPreferences.STOP_REMINDER, ProgressReminder.class);
        if (r == null) {
            MySharedPreferences.getInstance(null).setPreferenceItem(MySharedPreferences.STOP_REMINDER, new ProgressReminder());
        }

        registerGoogleCloudMessaging();

        //ViewServer.get(this).addWindow(this);

        Pedometer.mIsRunning = true;
        startService(new Intent(this, StepService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(className, "onResume");

        initServices();

        String userName = MySharedPreferences.getInstance(null).getUserName();
        if (userName.isEmpty()) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
        //ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
        cleanServices();
        Toast.makeText(this, "bye bye", Toast.LENGTH_SHORT).show();
        hasDestroy = true;
        MusicPlayer.getInstance().stopMusic(null);
        //ViewServer.get(this).removeWindow(this);
        stopService(new Intent(this, StepService.class));
        Pedometer.mIsRunning = false;
    }

    private void initServices() {
        Speak.getInstance(this);
        DatabaseHandler.getInstance(this);
        MySharedPreferences.getInstance(this);
        MyPedometerPreferences.getInstance(this);
        ResourceGetter.getInstance(this);
        MyWidgets.initContext(this);
    }

    private void cleanServices() {
        Speak.getInstance(null).clean();
        DatabaseHandler.getInstance(null).clean();
        MySharedPreferences.getInstance(null).clean();
        MyPedometerPreferences.getInstance(null).clean();
        MyWidgets.releaseContext();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 1) {
            if (!MySharedPreferences.getInstance(null).getFirstTime()) {
                Intent i = new Intent(this, ActivityRewardWelcome.class);
                startActivity(i);
                MySharedPreferences.getInstance(null).setFirstTime(true);
            }
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit? This will stop ongoing goal.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                MainActivity.super.onBackPressed();
            }
        }).create().show();
    }

    private void registerGoogleCloudMessaging() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.isEmpty()) {
            GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
        } else {
            Log.d(className, "Already registered");
            FragmentHome.DeviceToken = regId;
        }

        Log.d(className, "RegistrationId: " + regId);
    }
}
