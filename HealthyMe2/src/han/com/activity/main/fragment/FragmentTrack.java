package han.com.activity.main.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
import han.com.activity.music.ActivityMusic;
import han.com.activity.reward.remind.DialogReportReward;
import han.com.activity.track.goals.ActivityAddGoal;
import han.com.activity.track.goals.ActivitySavedGoals;
import han.com.activity.track.reminder.ProgressReminder;
import han.com.activity.track.settingmenu.ActivityTrackSettingMenu;
import han.com.activity.track.watch.TrackWatch;
import han.com.datapool.CurrentGoal;
import han.com.datapool.MyPedometerPreferences;
import han.com.datapool.MySharedPreferences;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.Reward;
import han.com.db.UserGoal;
import han.com.gps.InterfaceReportGpsDistance;
import han.com.gps.InterfaceTrackingReadyReport;
import han.com.gps.ListenerGpsLocation;
import han.com.gps.OutsideLocationTracker;
import han.com.music.MusicPlayer;
import han.com.step.InterfaceReportStep;
import han.com.step.SingletonListenerStep;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author han
 */
public class FragmentTrack extends Fragment implements InterfaceReportGpsDistance, InterfaceReportStep, InterfaceTrackingReadyReport {

    private static final String className = FragmentTrack.class.getName();
    private static Handler updateMusicInfo;

    public static Handler getUpdateMusicInfo() {
        return updateMusicInfo;
    }
    private TextView trackName, goalValue, goalUnit, subGoal3Value, subGoal1Value, subGoal2Value, trackProgressValue, trackProgressUnit, musicName;
    private ImageView subGoal1Image, subGoal2Image, sportTypeImage;
    private ImageButton buttonStart, buttonStop, buttonAddGoal;
    private ViewTrackProgress viewTrackProgressBar;
    private OutsideLocationTracker locationTracker;
    private ListenerGpsLocation gpsLocationListener;
    private Timer trackTimer;
    private Handler updateTimeDisplayHandler, updateProgressDisplayHandler, finishTrackHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");

        locationTracker = new OutsideLocationTracker(getActivity());
        locationTracker.setReportInitiation(this);

        gpsLocationListener = new ListenerGpsLocation(this);

        SingletonListenerStep.getInstance().setReportStep(this);

        updateTimeDisplayHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    displayDuration();
                }
            }
        };

        updateProgressDisplayHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object[] data = (Object[]) msg.obj;
                viewTrackProgressBar.updateProgress((Float) data[0]);
                trackProgressValue.setText((CharSequence) data[1]);
                trackProgressUnit.setText((CharSequence) data[2]);
            }
        };

        finishTrackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    finishTrack();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreateView");

        final View myFragmentView = inflater.inflate(R.layout.fragment_track, container, false);
        MyWidgets.makeFragmentTitle(myFragmentView, "Track");

        //test
        TextView title = (TextView) myFragmentView.findViewById(R.id.title_fragment_text);
        title.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                //Intent i = new Intent(getActivity(), Pedometer.class);
                //startActivity(i);
            }
        });
        //test end

        LinearLayout settingAll = (LinearLayout) myFragmentView.findViewById(R.id.fragment_setting_item_1);
        ImageView itemImage = (ImageView) settingAll.findViewWithTag("item_image");
        itemImage.setImageResource(R.drawable.ic_setting_all);
        settingAll.setVisibility(View.VISIBLE);
        settingAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityTrackSettingMenu.class);
                startActivity(i);
            }
        });

        LinearLayout settingMusic = (LinearLayout) myFragmentView.findViewById(R.id.setting_item_music);
        settingMusic.setVisibility(View.VISIBLE);
        settingMusic.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), ActivityMusic.class);
                startActivity(i);
            }
        });

        boolean scale = false;
        float scaleVlaue = 0;
        if (Values.DisplayDensity < 2) {
            scale = true;
            scaleVlaue = Values.DisplayDensity / 2f;
        }

        viewTrackProgressBar = new ViewTrackProgress(getActivity(), 500, 250, scale, scaleVlaue);

        LinearLayout layout = (LinearLayout) myFragmentView.findViewById(R.id.frag_track_track_progress_container);
        layout.addView(viewTrackProgressBar);

        trackName = (TextView) myFragmentView.findViewById(R.id.frag_track_track_name);

        goalValue = (TextView) myFragmentView.findViewById(R.id.frag_track_goal_value);
        goalUnit = (TextView) myFragmentView.findViewById(R.id.frag_track_goal_unit);

        subGoal1Value = (TextView) myFragmentView.findViewById(R.id.frag_track_sub_goal_1_value);
        subGoal2Value = (TextView) myFragmentView.findViewById(R.id.frag_track_sub_goal_2_value);
        subGoal3Value = (TextView) myFragmentView.findViewById(R.id.frag_track_sub_goal_3_value);

        subGoal1Image = (ImageView) myFragmentView.findViewById(R.id.frag_track_sub_goal_1_image);
        subGoal2Image = (ImageView) myFragmentView.findViewById(R.id.frag_track_sub_goal_2_image);

        sportTypeImage = (ImageView) myFragmentView.findViewById(R.id.frag_track_sport_image);

        trackProgressValue = (TextView) myFragmentView.findViewById(R.id.frag_track_track_progress_value);
        trackProgressUnit = (TextView) myFragmentView.findViewById(R.id.frag_track_track_progress_unit);

        buttonStart = (ImageButton) myFragmentView.findViewById(R.id.button_play_music);
        buttonStart.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (CurrentGoal.getGoalData() == null) {
                    String s = "Please select your goal first or add a new goal!";
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    Speak.getInstance(null).speak(s);
                    return;
                }

                if (!CurrentGoal.isTrackIsStarted()) {
                    int module = MySharedPreferences.getInstance(null).getTrackingModule();
                    if (module == MySharedPreferences.TRACKING_MODULE_BOTH_INDEX) {
                        gpsLocationListener.setProgressDistance(CurrentGoal.getCurrentDistance());//for both restart or continue
                        locationTracker.startListenLocation(gpsLocationListener);
                    } else if (module == MySharedPreferences.TRACKING_MODULE_PEDO_INDEX) {
                        SingletonListenerStep.getInstance().setProgressDistance(CurrentGoal.getCurrentDistance());//for both restart or continue
                        startTrack();
                    } else {
                        Log.e(className, "MySharedPreferences.TRACKING_MODULE is not set");
                    }
                }
            }
        });

        buttonStop = (ImageButton) myFragmentView.findViewById(R.id.button_pause_music);
        buttonStop.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (CurrentGoal.getGoalRecord() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Stop Current Goal?")
                            .setMessage("Are you sure you want to stop current tracking?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    stopTrack();
                                    Toast.makeText(getActivity(), "goal is cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();

                    return;
                }

                if (CurrentGoal.isTrackIsStarted()) {
                    stopTrack();
                }
            }
        });

        buttonAddGoal = (ImageButton) myFragmentView.findViewById(R.id.frag_track_goal_button);
        buttonAddGoal.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (CurrentGoal.getGoalRecord() != null) {
                    String s = "Please stop your current goal first";
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    Speak.getInstance(null).speak(s);
                    return;
                }
                Intent i = new Intent(getActivity(), ActivityAddGoal.class);
                startActivity(i);
            }
        });

        musicName = (TextView) myFragmentView.findViewById(R.id.frag_track_music_name);

        return myFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(className, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(className, "onStop");
        updateMusicInfo = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
        stopTrack();
        CurrentGoal.setGoalData(null);
    }

    private void updateControlButtonAndMusicInfo() {
        if (CurrentGoal.isTrackIsStarted()) {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
        } else {
            buttonStart.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.GONE);
        }

        if (MusicPlayer.getInstance().isPlaying()) {
            musicName.setVisibility(View.VISIBLE);
            musicName.setText(MusicPlayer.getInstance().getCurrentAuthor() + " - " + MusicPlayer.getInstance().getCurrentTitle());
        } else {
            musicName.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");
        CurrentGoal.log();

        if (CurrentGoal.getGoalData() == null) {
            UserGoal coachGoal = UserGoal.loadNextGoachGoal(DatabaseHandler.getInstance(null).getReadableDatabase());
            if (coachGoal != null) {
                CurrentGoal.setGoalData(coachGoal);
                CurrentGoal.clearCurrentTrackingInfo();
                Speak.getInstance(null).speak("you have a coach goal to finish");
                Toast.makeText(getActivity(), "You have a Coach goal to finish", Toast.LENGTH_SHORT).show();

                if (!UserGoal.isFinishedOneCoachGoalToday(DatabaseHandler.getInstance(null).getReadableDatabase())) {
                    MyWidgets.showNotification(R.drawable.ic_notification_p0_small,
                            R.drawable.ic_notification_p0,
                            "You have a Coach goal to finish today!",
                            "Hello " + MySharedPreferences.getInstance(null).getUserName(),
                            "You have a Coach goal to finish today",
                            "",
                            MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS,
                            true);
                }
            }
        }

        updateControlButtonAndMusicInfo();
        refreshAllTrackDisplay();
        displayGoalInfo();

        if (updateMusicInfo == null) {
            updateMusicInfo = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        updateControlButtonAndMusicInfo();
                    }
                }
            };
        }
    }

    private void updateSubGoalValueDisplay() {
        String currentGoalType = CurrentGoal.getGoalData().getGoalType();
        if (currentGoalType.equals(UserGoal.GOAL_TYPE_DISTANCE)) {
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");
            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));

        } else if (currentGoalType.equals(UserGoal.GOAL_TYPE_TIME)) {
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");
            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));
            subGoal3Value.setText(CurrentGoal.getCurrentDistanceString() + " " + Values.MetricMode);

        } else if (currentGoalType.equals(UserGoal.GOAL_TYPE_STEP)) {
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");
            subGoal2Value.setText(CurrentGoal.getCurrentDistanceString() + " " + Values.MetricMode);

        } else if (currentGoalType.equals(UserGoal.GOAL_TYPE_CALORIES)) {
            subGoal1Value.setText(CurrentGoal.getCurrentDistanceString() + " " + Values.MetricMode);
            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));
        }
    }

    private void checkProgress() {
        int percent = (int) CurrentGoal.getProgressOutOfOneHundred();
        if (percent >= 100) {
            finishTrackHandler.sendEmptyMessage(0);
            return;
        }
        Log.d(className, "checkProgress(): " + percent);

        String s = "you have finished " + percent + "%,";

        if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_DISTANCE)) {
            if (Values.MetricMode.equals("mile")) {
                s += "and that is " + (int) CurrentGoal.getCurrentDistance() + " feet";
            } else {
                s += "and that is " + (int) CurrentGoal.getCurrentDistance() + " meters";
            }
        } else if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_TIME)) {
            s += "and that is " + (int) CurrentGoal.getCurrentTotalTime() / 1000 + " seconds";
        } else if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_STEP)) {
            s += "and that is " + CurrentGoal.getCurrentTotalSteps() + " steps";
        }

        ProgressReminder stopReminder = (ProgressReminder) MySharedPreferences.getInstance(null)
                .getPreferenceItem(MySharedPreferences.STOP_REMINDER, ProgressReminder.class);

        boolean[] checkPoints = stopReminder.getPressedCheckpoints();

        if (!CurrentGoal.CheckPointsReported[1] && percent >= 25) {
            if (CurrentGoal.getGoalData().getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
                MyWidgets.cancelNotification(MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS);
                MyWidgets.showNotification(R.drawable.ic_notification_p1_small,
                        R.drawable.ic_notification_p1,
                        "Your seed is growing!",
                        "Your seed is growing",
                        "",
                        "25%",
                        MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS,
                        true);
            }
            CurrentGoal.CheckPointsReported[1] = true;

            if (stopReminder.isTurnedOn()) {
                if (checkPoints[1]) {
                    Speak.getInstance(null).speak(s);
                }
            }

        } else if (!CurrentGoal.CheckPointsReported[2] && percent >= 50) {
            if (CurrentGoal.getGoalData().getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
                MyWidgets.cancelNotification(MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS);
                MyWidgets.showNotification(R.drawable.ic_notification_p2_small,
                        R.drawable.ic_notification_p2,
                        "Your seed is growing!",
                        "Your seed is growing",
                        "",
                        "50%",
                        MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS,
                        true);
            }
            CurrentGoal.CheckPointsReported[2] = true;

            if (stopReminder.isTurnedOn()) {
                if (checkPoints[2]) {
                    Speak.getInstance(null).speak(s);
                }
            }

        } else if (!CurrentGoal.CheckPointsReported[3] && percent >= 75) {
            if (CurrentGoal.getGoalData().getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
                MyWidgets.cancelNotification(MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS);
                MyWidgets.showNotification(R.drawable.ic_notification_p3_small,
                        R.drawable.ic_notification_p3,
                        "Your seed is growing!",
                        "Your seed is growing",
                        "",
                        "75%",
                        MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS,
                        true);
            }
            CurrentGoal.CheckPointsReported[3] = true;

            if (stopReminder.isTurnedOn()) {
                if (checkPoints[3]) {
                    Speak.getInstance(null).speak(s);
                }
            }
        }
    }

    public void reportDistance(float actualDistance) {
        try {
            CurrentGoal.updateCurrentDistanceProgress(actualDistance, updateProgressDisplayHandler);
        } catch (Exception ex) {
            Log.wtf(className, ex);
        }

        checkProgress();

        updateSubGoalValueDisplay();
    }

    public void reportStep(int totalStep, float totalDistance) {
        try {
            CurrentGoal.updateCurrentStepProgress(totalStep, SingletonListenerStep.getInstance().getTotalCalories(), updateProgressDisplayHandler);
        } catch (Exception ex) {
            Log.wtf(className, ex);
        }

        if (MySharedPreferences.getInstance(null).getTrackingModule() == MySharedPreferences.TRACKING_MODULE_PEDO_INDEX) {
            try {
                CurrentGoal.updateCurrentDistanceProgress(totalDistance, updateProgressDisplayHandler);
            } catch (Exception ex) {
                Log.wtf(className, ex);
            }

            checkProgress();
        }

        updateSubGoalValueDisplay();
    }

    public void gpsIsReady() {
        startTrack();
    }

    private void displayGoalInfo() {
        UserGoal goal = CurrentGoal.getGoalData();
        if (goal == null) {
            return;
        }

        if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_STEP)) {
            goalValue.setText(String.valueOf((int) goal.getGoalValue()));
        } else {
            goalValue.setText(String.valueOf(goal.getGoalValue()));
        }
        goalUnit.setText(goal.getGoalUnit());

        String tempGoalName = goal.getGoalName();
        if (goal.getGoalOrder() > 0) {
            tempGoalName += " " + goal.getGoalOrder();
        }
        trackName.setText(tempGoalName);
    }

    private void startTrack() {
        Log.d(className, "startTrack");
        CurrentGoal.setTrackIsStarted(true);

        boolean isNewTrack = false;

        if (CurrentGoal.getGoalRecord() == null) {
            if (CurrentGoal.getGoalData() == null) {
                Log.wtf(className, "CurrentGoal.GoalData is null");
            }
            startNewGoal();
            isNewTrack = true;
        }

        if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_DISTANCE)) {
            if (Values.MetricMode.equals("mile")) {
                CurrentGoal.setTargetDistanceMetersOrFeet(CurrentGoal.getGoalData().getGoalValue() * Values.MILE_FOR_FEET);
            } else {
                CurrentGoal.setTargetDistanceMetersOrFeet(CurrentGoal.getGoalData().getGoalValue() * 1000f);
            }
        } else if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_TIME)) {
            CurrentGoal.setTargetTimemillisecond((long) CurrentGoal.getGoalData().getGoalValue() * 60 * 1000);
        } else if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_STEP)) {
            CurrentGoal.setTargetSteps((int) CurrentGoal.getGoalData().getGoalValue());
        } else if (CurrentGoal.getGoalData().getGoalType().equals(UserGoal.GOAL_TYPE_CALORIES)) {
            CurrentGoal.setTargetCalories(CurrentGoal.getGoalData().getGoalValue());
        }

        if (isNewTrack) {
            TrackWatch.getInstance().start();

            Speak.getInstance(null).speak("new tracking started");

        } else {
            if (TrackWatch.getInstance().isPaused()) {
                TrackWatch.getInstance().resume();
            }
            Speak.getInstance(null).speak("tracking resumed");
        }

        startTimers();

        updateControlButtonAndMusicInfo();

        String sensitivity = MyPedometerPreferences.getInstance(null).getSensitivity();
        if (sensitivity.equals(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[0][1])) {
            Speak.getInstance(null).speak("podometer should be at " + MyPedometerPreferences.PEDOMETER_POSITION_VALUES[0][0] + "position");
        } else if (sensitivity.equals(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[1][1])) {
            Speak.getInstance(null).speak("podometer should be at " + MyPedometerPreferences.PEDOMETER_POSITION_VALUES[1][0] + "position");
        }

        if (CurrentGoal.getGoalData().getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
            MyWidgets.cancelNotification(MyWidgets.NOTIFICATION_ID_WEEK_PROGRESS);
            MyWidgets.showNotification(R.drawable.ic_notification_p0_small,
                    R.drawable.ic_notification_p0,
                    "Your seed is planted!",
                    "Your seed is planted",
                    "",
                    "0%",
                    MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS,
                    true);
        }
    }

    private void startNewGoal() {
        removeCurrentGoalRecord();

        CurrentGoal.setGoalRecord(GoalRecord.addNewGoalRecord(DatabaseHandler.getInstance(null).getWritableDatabase(),
                CurrentGoal.getGoalData().getGoalId(), System.currentTimeMillis()));
    }

    private void removeCurrentGoalRecord() {
        gpsLocationListener.clearRecord();
        SingletonListenerStep.getInstance().clearRecord();
        CurrentGoal.clearCurrentTrackingInfo();
    }

    private void pauseTrack() {
        Log.d(className, "pauseTrack");
        if (!CurrentGoal.isTrackIsStarted()) {
            return;
        }

        CurrentGoal.setTrackIsStarted(false);
        MusicPlayer.getInstance().stopMusic(null);
        updateControlButtonAndMusicInfo();

        if (!TrackWatch.getInstance().isPaused()) {
            TrackWatch.getInstance().pause();
        }
        stopTimers();
        locationTracker.end();
        snapshotTrackingProgress();

        if (CurrentGoal.getGoalData().getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
            MyWidgets.cancelNotification(MyWidgets.NOTIFICATION_ID_GOAL_PROGRESS);
        }
        //Speak.getInstance(null).speak("tracking paused");
    }

    private void snapshotTrackingProgress() {
        try {
            int module = MySharedPreferences.getInstance(null).getTrackingModule();
            if (module == MySharedPreferences.TRACKING_MODULE_BOTH_INDEX) {
                CurrentGoal.updateCurrentDistanceProgress(gpsLocationListener.getProgressDistance(), updateProgressDisplayHandler);
            } else if (module == MySharedPreferences.TRACKING_MODULE_PEDO_INDEX) {
                CurrentGoal.updateCurrentDistanceProgress(SingletonListenerStep.getInstance().getProgressDistance(), updateProgressDisplayHandler);
            }

            CurrentGoal.updateCurrentTimeProgress(TrackWatch.getInstance().getTime(), updateProgressDisplayHandler);
            CurrentGoal.updateCurrentStepProgress(SingletonListenerStep.getInstance().getTotalSteps(), SingletonListenerStep.getInstance().getTotalCalories(), updateProgressDisplayHandler);

        } catch (Exception ex) {
            Log.wtf(className, ex);
        }
    }

    private void stopTrack() {
        if (CurrentGoal.isTrackIsStarted()) {
            pauseTrack();
        } else {
            return;
        }

        if (CurrentGoal.getGoalRecord() == null) {
            return;
        }

        GoalRecord.endGoal(DatabaseHandler.getInstance(null).getWritableDatabase(),
                CurrentGoal.getGoalRecord().getRecordId(), System.currentTimeMillis(), GoalRecord.COMPLETE_FAIL,
                CurrentGoal.getCurrentTotalTime(), CurrentGoal.getCurrentTotalSteps(), CurrentGoal.getCurrentTotalCalories());

        removeCurrentGoalRecord();

        Speak.getInstance(null).speak("tracking cancelld");
        refreshAllTrackDisplay();

        if (FragmentReward.getCheckWeekHandler() != null) {
            FragmentReward.getCheckWeekHandler().sendEmptyMessage(0);
        }
    }

    private void finishTrack() {
        if (CurrentGoal.isTrackIsStarted()) {
            pauseTrack();
        } else {
            return;
        }

        UserGoal currentGoal = CurrentGoal.getGoalData();
        if (currentGoal.getGoalName().equals(UserGoal.GOAL_NAME_COACH)) {
            UserGoal.finishCoachGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), currentGoal.getGoalId());
            CurrentGoal.setGoalData(null);
            if (FragmentReward.getLoadRewardsHandler() != null) {
                FragmentReward.getLoadRewardsHandler().sendEmptyMessage(0);
            }

            int finishedCoachGoal = UserGoal.getTotalFinishedCoachGoalsNumber(DatabaseHandler.getInstance(null).getReadableDatabase());

            Reward reward = Reward.getObtainedRewardByNumber(DatabaseHandler.getInstance(null).getReadableDatabase(),
                    finishedCoachGoal);

            if (reward != null) {
                String rewardContent = "Woohoo! you have obtained " + finishedCoachGoal
                        + " flowers, and now you can get " + reward.getRewardName();

                Intent i = new Intent(getActivity(), DialogReportReward.class);
                i.putExtra("dialog_reward_name", reward.getRewardName());
                i.putExtra("dialog_reward_content", rewardContent);
                startActivity(i);
            }

            if (FragmentReward.getCheckWeekHandler() != null) {
                FragmentReward.getCheckWeekHandler().sendEmptyMessage(1);
            }
        } else {
            if (FragmentReward.getCheckWeekHandler() != null) {
                FragmentReward.getCheckWeekHandler().sendEmptyMessage(0);
            }
        }

        GoalRecord.endGoal(DatabaseHandler.getInstance(null).getWritableDatabase(),
                CurrentGoal.getGoalRecord().getRecordId(), System.currentTimeMillis(), GoalRecord.COMPLETE_SUCCESS,
                CurrentGoal.getCurrentTotalTime(), CurrentGoal.getCurrentTotalSteps(), CurrentGoal.getCurrentTotalCalories());

        removeCurrentGoalRecord();

        ProgressReminder stopReminder = (ProgressReminder) MySharedPreferences.getInstance(null)
                .getPreferenceItem(MySharedPreferences.STOP_REMINDER, ProgressReminder.class);
        if (stopReminder.isTurnedOn()) {
            Speak.getInstance(null).speak("tracking finished");
        } else {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
            r.play();
        }

        refreshAllTrackDisplay();
    }

    private void refreshAllTrackDisplay() {
        Log.d(className, "refreshAllTrackDisplay");

        trackProgressValue.setText("0.00");
        trackProgressUnit.setText(Values.MetricMode);
        subGoal3Value.setText("00:00:00");
        subGoal1Value.setText("000 cal");
        subGoal2Value.setText("0000");

        UserGoal goal = CurrentGoal.getGoalData();
        if (goal == null) {
            return;
        }

        if (ActivitySavedGoals.getSportImagesBlack() != null) {
            sportTypeImage.setImageResource(ActivitySavedGoals.getSportImagesBlack().get(goal.getGoalName()));
        }

        if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_DISTANCE)) {
            trackProgressValue.setText(CurrentGoal.getCurrentDistanceString());
            trackProgressUnit.setText(goal.getGoalUnit());
            viewTrackProgressBar.updateProgress(CurrentGoal.getProgressOutOfOneHundred());

            subGoal3Value.setText(CurrentGoal.getCurrentTotalTimeString());
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");

            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));

            subGoal1Image.setImageResource(R.drawable.ic_track_calorie);
            subGoal2Image.setImageResource(R.drawable.ic_track_steps);

        } else if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_TIME)) {
            trackProgressValue.setText(CurrentGoal.getCurrentTotalTimeString());
            trackProgressUnit.setText("");
            viewTrackProgressBar.updateProgress(CurrentGoal.getProgressOutOfOneHundred());

            subGoal3Value.setText(CurrentGoal.getCurrentDistanceString() + " " + goal.getGoalUnit());
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");

            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));

            subGoal1Image.setImageResource(R.drawable.ic_track_calorie);
            subGoal2Image.setImageResource(R.drawable.ic_track_steps);

        } else if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_STEP)) {
            trackProgressValue.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));
            trackProgressUnit.setText("steps");
            viewTrackProgressBar.updateProgress(CurrentGoal.getProgressOutOfOneHundred());

            subGoal3Value.setText(CurrentGoal.getCurrentTotalTimeString());
            subGoal1Value.setText(CurrentGoal.getCurrentTotalCaloriesString() + " cal");

            subGoal2Value.setText(CurrentGoal.getCurrentDistanceString() + " " + goal.getGoalUnit());

            subGoal1Image.setImageResource(R.drawable.ic_track_calorie);
            subGoal2Image.setImageResource(R.drawable.ic_track_distance);

        } else if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_CALORIES)) {
            trackProgressValue.setText(CurrentGoal.getCurrentTotalCaloriesString());
            trackProgressUnit.setText("cal");
            viewTrackProgressBar.updateProgress(CurrentGoal.getProgressOutOfOneHundred());

            subGoal3Value.setText(CurrentGoal.getCurrentTotalTimeString());
            subGoal1Value.setText(CurrentGoal.getCurrentDistanceString() + " " + goal.getGoalUnit());

            subGoal2Value.setText(String.valueOf(CurrentGoal.getCurrentTotalSteps()));

            subGoal1Image.setImageResource(R.drawable.ic_track_distance);
            subGoal2Image.setImageResource(R.drawable.ic_track_steps);
        }
    }

    private void startTimers() {
        stopTimers();

        trackTimer = new Timer();
        trackTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!CurrentGoal.isTrackIsStarted()) {
                    return;
                }
                try {
                    CurrentGoal.updateCurrentTimeProgress(TrackWatch.getInstance().getTime(), updateProgressDisplayHandler);
                } catch (Exception ex) {
                    Log.wtf(className, ex);
                }

                updateTimeDisplayHandler.sendEmptyMessage(0);
                //Log.d(className, CurrentGoal.getCurrentTotalTimeString());

                checkProgress();
            }
        }, 0, 1000);
    }

    private void stopTimers() {
        if (trackTimer != null) {
            trackTimer.cancel();
            trackTimer = null;
        }
    }

    private void displayDuration() {
        String currentGoalType = CurrentGoal.getGoalData().getGoalType();
        if (currentGoalType.equals(UserGoal.GOAL_TYPE_DISTANCE)
                || currentGoalType.equals(UserGoal.GOAL_TYPE_STEP)
                || currentGoalType.equals(UserGoal.GOAL_TYPE_CALORIES)) {
            subGoal3Value.setText(CurrentGoal.getCurrentTotalTimeString());
        }
    }
}
